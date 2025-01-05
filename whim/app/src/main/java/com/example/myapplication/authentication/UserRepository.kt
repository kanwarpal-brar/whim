package com.example.myapplication.authentication

import android.util.Log
import com.example.myapplication.authentication.helpers.convertDocumentToUser
import com.example.myapplication.authentication.helpers.convertDocumentsToFriends
import com.example.myapplication.authentication.helpers.createFriendUpdate
import com.example.myapplication.authentication.helpers.convertDocumentsToIDs
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

// Using await to find out if firebase database task is successful
//    https://stackoverflow.com/questions/64896890/how-to-know-whether-task-is-failed-in-taskt-api-using-when-kotlin-coroutines
class UserRepository(private val db: FirebaseFirestore) : UserDAO {
    private val userCollection: String = "users"
    private val friendSubcollection: String = "friends"
    private val userNameCollection: String = "usernames_map"
    private val eventUserCollection: String = "events_users"

    override suspend fun takeUserName(userName: String): Result<Unit> {
        return try {
            val docRef = db.collection(userNameCollection).document(userName)
            val docSnapshot = docRef.get().await()
            println("docSnapshot: $docSnapshot")
            if (docSnapshot.exists()) {
                val userNameTakenException = Exception("User Name taken.")
                Result.failure(userNameTakenException)
            } else {
                val userNameData = mapOf(
                    "userID" to null
                )
                db.collection(userNameCollection).document(userName).set(userNameData).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEmailAvailable(email: String): Result<Unit> {
        return try {
            val emailQuery = db.collection(userCollection).whereEqualTo("email", email)
            val querySnapshot = emailQuery.get().await()
            if (querySnapshot.isEmpty) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Email not available"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(
        userID: String,
        email: String,
        userName: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        try {
            val userData = mapOf(
                "email" to email,
                "userName" to userName,
                "firstName" to firstName,
                "lastName" to lastName,
            )
            db.collection(userCollection)
                .document(userID)
                .set(userData).await()

        } catch (e: Exception) {
            db.collection(userNameCollection)
                .document(userName)
                .delete().await()
            return Result.failure(e)
        }

        try {
            val userNameDocRef = db.collection(userNameCollection).document(userName)
            db.runTransaction{ transaction ->
                val snapshot = transaction.get(userNameDocRef)
                val oldUID = snapshot.getString("userID")
                if (oldUID == null) {
                    transaction.update(userNameDocRef, "userID", userID)
                } else {
                    throw FirebaseFirestoreException(
                        "User Name already bound to an existing account",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }
            }.await()
            return Result.success(Unit)
        } catch (e: Exception) {
            db.collection(userCollection)
                .document(userID)
                .delete().await()
            if (!e.toString().contains("User Name already bound to an existing account")) {
                db.collection(userNameCollection)
                    .document(userName)
                    .delete().await()
            }
            return Result.failure(e)
        }
    }

    override suspend fun deleteUser(userID: String): Result<Unit> {
        return try {
            val friendList = getFriends(userID).getOrNull()
            val userDocRef = db.collection(userCollection)
                .document(userID)
            val userDoc = userDocRef.get().await()
            val userName = userDoc.get("userName").toString()
            userDocRef.delete().await()

            db.collection(userNameCollection)
                .document(userName)
                .delete().await()

            // TO-DO: Update Due to Check-in/Check-out: delete Joined Events entries in eventUserCollection

            // delete fields in Friends' friendSubcollection's AND User's friendSubcollection
            if (friendList != null) {
                for (friend in friendList) {
                    val friendID = friend.userID
                    db.collection(userCollection)
                        .document(friendID)
                        .collection(friendSubcollection)
                        .document(userID)
                        .delete().await()
                    db.collection(userCollection)
                        .document(userID)
                        .collection(friendSubcollection)
                        .document(friendID)
                        .delete().await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userID: String): Result<User> {
        return try {
            val userDocSnapshot = db.collection(userCollection)
                .document(userID)
                .get().await()
            if (userDocSnapshot.exists()) {
                Result.success(convertDocumentToUser(userDocSnapshot))
            } else {
                Result.failure(Exception("User $userID not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun updateUserNameCollection(userID: String, userName: String) {
        val userNameDocRef = db.collection(userNameCollection).document(userName)
        db.runTransaction{ transaction ->
            val snapshot = transaction.get(userNameDocRef)
            val oldUID = snapshot.getString("userID")
            if (oldUID == null) {
                transaction.update(userNameDocRef, "userID", userID)
            } else {
                throw FirebaseFirestoreException(
                    "User Name already bound to an existing account",
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
        }.await()
        val oldUserDoc = db.collection(userCollection).document(userID).get().await()
        val oldUserName = oldUserDoc.get("userName").toString()
        db.collection(userNameCollection)
            .document(oldUserName)
            .delete().await()
    }

    override suspend fun updateUser(userID: String,
                                    email: String?,
                                    userName: String?,
                                    firstName: String?,
                                    lastName: String?
    ): Result<Unit> {
        return try {
            if (userName != null) { updateUserNameCollection(userID, userName) }

            val userUpdates = buildMap() {
                if(email != null) { put("email", email) }
                if(userName != null) { put("userName", userName) }
                if(firstName != null) { put("firstName", firstName) }
                if(lastName != null) { put("lastName", lastName) }
            }

            db.collection(userCollection)
                .document(userID)
                .update(userUpdates).await()

            val friendList = getFriends(userID).getOrNull()
            // update fields in friends
            if (friendList != null) {
                for (friend in friendList) {
                    val friendID = friend.userID
                    db.collection(userCollection)
                        .document(friendID)
                        .collection(friendSubcollection)
                        .document(userID)
                        .update(userUpdates).await()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendees(eventID: String): Result<List<User>> {
        val userIDList: List<String>
        val userList: MutableList<User> = mutableListOf()
        try {
            val eventUserQuery = db.collection(eventUserCollection).whereEqualTo("eventId", eventID)
            val eventUserSnapshot = eventUserQuery.get().await()
            userIDList = convertDocumentsToIDs(eventUserSnapshot.documents)
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return try {
            for (userID in userIDList) {
                val user = getUser(userID).getOrNull()
                if (user != null) { userList.add(user) }
            }
            Result.success(userList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAttendanceStatus(eventID: String, userID: String
    ): Result<AttendanceStatus> {
        val eventUserKey = userID + "_" + eventID
        val docSnapshot: DocumentSnapshot
        try {
            docSnapshot = db.collection(eventUserCollection)
                .document(eventUserKey)
                .get().await()
        } catch (e: Exception) {
            return Result.failure(e)
        }

        // Note: Order of "else if" matters, (check_in == true && check_out == true) indicates CHECKED_OUT
        return if (!docSnapshot.exists()) {
            Result.success(AttendanceStatus.NOT_RESERVED)
        } else if (docSnapshot.get("check_in") == false){
            Result.success(AttendanceStatus.RESERVED)
        } else if (docSnapshot.get("check_out") == true) {
            Result.success(AttendanceStatus.CHECKED_OUT)
        } else if (docSnapshot.get("check_in") == true) {
            Result.success(AttendanceStatus.CHECKED_IN)
        } else {
            Result.failure(IllegalStateException("Attendance Status Invalid"))
        }
    }

    override suspend fun requestFriend(userID: String, friendUserName: String): Result<Unit> {
        val user: User? = getUser(userID).getOrNull()
        val friend: User? = getUserByUserName(friendUserName).getOrNull()
        if ((user == null) or (friend == null)) {
            Log.d("UserRepo - Fail Get Friend", "User Repo- Fail to Get Friend")
            return Result.failure(Exception("Couldn't get User or Friend User Doc"))
        } else {
            Log.d("UserRepo - Got Friend", "User Repo- Got sFriend")
            return addPendingFriend(user!!, friend!!)
        }
    }

    override suspend fun acceptFriend(userID: String, friendUserName: String): Result<Unit> {
        Log.d("Friends", "Accept Friend Called in UserRepository")
        val friend: User? = getUserByUserName(friendUserName).getOrNull()
        val user: User? = getUser(userID).getOrNull()
        if ((user == null) or (friend == null)) {
            if(user == null) {
                Log.d("UserNull", "user is null")
            }
            if(friend == null) {
                Log.d("FriendNull", "friend is null")
            }
            Log.d("Friends", "Null user or friend")
            return Result.failure(Exception("Couldn't get User's Doc"))
        }

        val userFriendsRef = db.collection(userCollection).document(user!!.userID).collection(friendSubcollection)
        val friendFriendsRef = db.collection(userCollection).document(friend!!.userID).collection(friendSubcollection)
        val statusUpdate = mapOf(
            "friendStatus" to FriendProfile.CONFIRMED
        )
        try {
            userFriendsRef.document(friend.userID).update(statusUpdate).await()
            friendFriendsRef.document(user.userID).update(statusUpdate).await()
            Log.d("Friends","Friend Ref Call Made")
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Error - Friends", "Failed to Accept Request", e)
            return Result.failure(e)
        }
    }

    override suspend fun getFriends(userID: String): Result<List<FriendProfile>> {
        val userFriendsRef = db.collection(userCollection).document(userID).collection(friendSubcollection)
        try {
            val friendSnapshots = userFriendsRef.get().await()
            return Result.success(convertDocumentsToFriends(friendSnapshots.documents))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun submitHostRating(userID: String, rating: Double): Result<Unit> {
        assert(rating in 1.0..5.0) {
            return Result.failure(Exception("Rating must be between 0 and 5"))
        } // 5-star rating system

        val userRef = db.collection(userCollection).document(userID)
        try {
            db.runTransaction {
                val snapshot = it.get(userRef)
                val currentRating: Double = snapshot.getDouble("hostRating") ?: 0.0
                val currentCount: Double = (snapshot.getLong("countHostRating") ?: 0)
                    .toDouble()
                val newCount = currentCount + 1
                val newRating: Double = (currentRating * currentCount + rating) / newCount
                it.update(userRef, mapOf(
                    "hostRating" to newRating,
                    "countHostRating" to newCount
                ))
            }.addOnFailureListener{
                throw it
            }.await()
        } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(Unit)
    }

    override suspend fun removeFriend(userID: String, friendUserName: String): Result<Unit> {
        val friend: User? = getUserByUserName(friendUserName).getOrNull()
        val user: User? = getUser(userID).getOrNull()
        if ((user == null) or (friend == null)) { return Result.failure(Exception("Couldn't get User's Doc")) }

        val userFriendsRef = db.collection(userCollection).document(user!!.userID).collection(friendSubcollection)
        val friendFriendsRef = db.collection(userCollection).document(friend!!.userID).collection(friendSubcollection)
        try {
            userFriendsRef.document(friend.userID).delete().await()
            friendFriendsRef.document(user.userID).delete().await()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun getFriendsRef(userID: String): CollectionReference {
        return  db.collection(userCollection).document(userID).collection(friendSubcollection)
    }

    suspend private fun addPendingFriend(user: User, friend: User): Result<Unit> {
        val userFriendsRef = db.collection(userCollection).document(user.userID).collection(friendSubcollection)
        val friendFriendsRef = db.collection(userCollection).document(friend.userID).collection(friendSubcollection)
        Log.d("Friend Added", "Friend Added")
        try {
            val userData = createFriendUpdate(user, FriendProfile.PENDING_REQUEST)
            val friendData = createFriendUpdate(friend, FriendProfile.PENDING_ANSWER)
            userFriendsRef.document(friend.userID).set(friendData).await()
            friendFriendsRef.document(user.userID).set(userData).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private suspend fun getUserByUserName(userName: String): Result<User> {
        val userNameDocRef = db.collection(userNameCollection).document(userName)
        var userID: String? = null
        try {
            val userNameDocSnapshot = userNameDocRef.get().await()
            if (userNameDocSnapshot.exists()) {
                userID = userNameDocSnapshot.getString("userID")!!
            } else {
                return Result.failure(Exception("Friend's User Name does not exist"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        val user = getUser(userID).getOrNull()
        if (user == null) { return Result.failure(Exception("No user found"))}
        return Result.success(user)
    }


}
