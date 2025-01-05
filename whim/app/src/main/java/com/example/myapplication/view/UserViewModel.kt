package com.example.myapplication.view

import androidx.lifecycle.ViewModel
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.authentication.AttendanceStatus
import com.example.myapplication.authentication.FriendProfile
import com.example.myapplication.authentication.User
import com.example.myapplication.authentication.UserProfile
import com.example.myapplication.authentication.UserRepository
import com.example.myapplication.authentication.UserDAO
import com.example.myapplication.authentication.helpers.convertDocumentsToFriends
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserViewModel(private var auth: FirebaseAuth,
                    private var userDao: UserDAO) : ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>(auth.currentUser)
    val userLiveData: LiveData<FirebaseUser?> = _user
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile
    private val _friends = MutableLiveData<List<FriendProfile>?>()
    val friends: LiveData<List<FriendProfile>?> = _friends

    private var _friendListener: ListenerRegistration? = null

    init {
        _user.value = null
        _userProfile.value = UserProfile(null, null)
        _friends.value = null
    }

    // https://firebase.google.com/docs/auth/android/manage-users#get_the_currently_signed-in_user
    // It is suggested to listen to token state changes to handle edge cases
    //   Note: Firebase's IdTokenListener is a finicky and gets called more times than necessary
    //         The if statements are there to circumvent unecessary calls
    private val firebaseTokenListener = FirebaseAuth.IdTokenListener {
        val lastUser = _user.value
        val curUser = auth.currentUser
        // only set _user.value if user changes
        if ((lastUser != null) and (curUser != null)) {
            // changed user
            if (lastUser!!.uid != curUser!!.uid) {
                viewModelScope.launch {
                    val curUserDoc = userDao.getUser(curUser.uid).getOrNull()
                    _user.value = curUser
                    _userProfile.value = UserProfile(curUser, curUserDoc)
                    _friends.value = userDao.getFriends(curUser.uid).getOrNull()
                    setFriendListener(curUser.uid)
                }
            }
        } else if ((lastUser == null) and (curUser == null)) {
            // no change
        } else {
            // change from signed in to signed out or signed out to signed in
            if (curUser != null) {
                viewModelScope.launch {
                    val curUserDoc = userDao.getUser(curUser.uid).getOrNull()
                    _user.value = curUser
                    _userProfile.value = UserProfile(curUser, curUserDoc)
                    _friends.value = userDao.getFriends(curUser.uid).getOrNull()
                    setFriendListener(curUser.uid)
                }
            } else {
                if (_friendListener != null) { _friendListener!!.remove() }
                _user.value = null
                _userProfile.value = UserProfile(null, null)
                _friends.value = null
            }
        }
    }

    suspend fun takeUserName(userName: String): Result<Unit> {
        return userDao.takeUserName(userName)
    }

    suspend fun createUser(email: String,
                           password: String,
                           userName: String,
                           firstName: String,
                           lastName: String): Result<Unit> {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch(e: Exception) {
            return Result.failure(e)
        }
        val UID = auth.currentUser!!.uid
        try {
            val createUserDocResult = userDao.createUser(
                userID = UID,
                email = email,
                userName = userName,
                firstName = firstName,
                lastName = lastName
            )

            return if (createUserDocResult.isSuccess) {
                fetchUser()
                Result.success(Unit)
            } else {
                auth.currentUser!!.delete()
                Result.failure(createUserDocResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            auth.currentUser!!.delete()
            return Result.failure(e)
        }
    }

    suspend fun isEmailAvailable(email: String): Result<Unit> {
        return userDao.isEmailAvailable(email)
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            fetchUser()
            Result.success(Unit)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userID: String): Result<User> {
        return userDao.getUser(userID)
    }

    private suspend fun fetchUser() {
        val curUser = auth.currentUser ?: return

        // If there was a user change, in the meantime. Don't update LiveData
        if (curUser.uid == auth.currentUser?.uid) {
            val userDoc = userDao.getUser(curUser.uid).getOrNull()
            if (userDoc != null) {
                _user.value = curUser
                _userProfile.value = UserProfile(curUser, userDoc)
                _friends.value = userDao.getFriends(curUser.uid).getOrNull()

                // listen to friends change
                setFriendListener(curUser.uid)

            }
            auth.addIdTokenListener(firebaseTokenListener)
        }
    }

    suspend fun getAttendees(eventID: String): Result<List<User>> {
        return userDao.getAttendees(eventID)
    }

    suspend fun getAttendanceStatus(eventID: String, userID: String): Result<AttendanceStatus> {
        return userDao.getAttendanceStatus(eventID, userID)
    }

    suspend fun getAttendanceStatus(eventID: String): Result<AttendanceStatus> {
        val userID = _user.value?.uid ?: return Result.failure(Exception("No user signed in"))
        return userDao.getAttendanceStatus(eventID, userID)
    }

    // Call only after createUser/sign-in
    fun sendVerificationEmail() {
        val user = _user.value ?: return
        val userDoc = _userProfile.value?.userDoc
        auth.useAppLanguage()
        user.sendEmailVerification()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Verification Email Sent.")
                }
            }
    }

    // DON'T USE
    // Works, but calling isEmailVerified() throws an error.
    fun sendVerificationAndUpdateEmail(newEmail: String) {
        val user = _user.value ?: return
        val userDoc = _userProfile.value?.userDoc
        auth.useAppLanguage()
        user.verifyBeforeUpdateEmail(newEmail)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Verification Email Sent.")
                }
            }
    }

    // reload() is needed otherwise isEmailVerified property of user never gets updated (until ~1hour later)
    // See: https://stackoverflow.com/questions/63257785/firebaseuser-isemailverified-is-always-returning-false-even-after-verifying-emai
    // Note: After testing for a long time, I still have no clue how to make this work when updating Email.
    //       But it is fine, I don't think we really need to update emails
    suspend fun isEmailVerified() {
        val curUser = _user.value
        curUser!!.reload().await()
        val reloadedUser = auth.currentUser
        if (reloadedUser!!.isEmailVerified) {
            userDao.updateUser(reloadedUser.uid, email = reloadedUser.email)
            _user.value = reloadedUser
            _userProfile.value = UserProfile(reloadedUser , _userProfile.value?.userDoc, email = reloadedUser.email)
        } else {
            Log.d(TAG, "Email has not been verified yet")
        }
    }


    suspend fun updateUserName(newName: String) {
        val curUser = _user.value ?: return
        val updateResult = userDao.updateUser(curUser.uid, userName = newName)
        if (updateResult.isSuccess) {
            val userDoc = userDao.getUser(curUser.uid).getOrNull()
            if (userDoc != null) {
                _user.value = auth.currentUser
                _userProfile.value = UserProfile(_user.value, userDoc)
            }
        }
    }

    suspend fun updateFirstLastName(newFirstName: String, newLastName: String) {
        val curUser = _user.value ?: return
        val updateResult = userDao.updateUser(curUser.uid, firstName = newFirstName, lastName = newLastName)
        if (updateResult.isSuccess) {
            val userDoc = userDao.getUser(curUser.uid).getOrNull()
            if (userDoc != null) {
                _user.value = auth.currentUser
                _userProfile.value = UserProfile(_user.value, userDoc)
            }
        }
    }

    suspend fun submitHostRating(rating: Double) {
        val curUser = _user.value ?: return
        submitHostRating(curUser.uid, rating)
    }

    suspend fun submitHostRating(userID: String, rating: Double) {
        try {
            assert(rating in 0.0..5.0)
        } catch (e: AssertionError) {
            Log.e(TAG, "Rating must be between 0 and 5, invalid rating: ${rating}")
        }
         { "Rating must be between 0 and 5" }
        userDao.submitHostRating(userID, rating)
    }

    fun updatePhotoUrl(newUrl: String) {
        val profileUpdate = userProfileChangeRequest {
            displayName = newUrl
        }
        val curUser = _user.value ?: return
        curUser.updateProfile(profileUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User Photo updated.")

                    // Handle case where user has changed in the meantime
                    if (curUser.uid == auth.currentUser?.uid) {
                        val oldUserProfile = _userProfile.value
                        _userProfile.value = UserProfile(curUser, oldUserProfile?.userDoc)
                    }
                }
            }
    }

    fun signOut() {
        if (_friendListener != null) { _friendListener!!.remove() }
        auth.removeIdTokenListener(firebaseTokenListener)
        auth.signOut()
        _user.value = null
        _userProfile.value = UserProfile(null, null)
    }

    // Ask the User to ReAuthenticate First for safety
    fun deleteCurrentUser() {
        if (_friendListener != null) { _friendListener!!.remove() }
        auth.removeIdTokenListener(firebaseTokenListener)
        val user = auth.currentUser ?: return
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "User account deleted.")
                viewModelScope.launch {
                    userDao.deleteUser(user.uid)
                }
            }
        }
        _user.value = null
        _userProfile.value = UserProfile(null, null)
    }

    suspend fun requestFriend(friendUserName: String): Result<Unit> {
        val user = _user.value ?: return Result.failure(IllegalStateException("User not found"))
        return try {
            val result = userDao.requestFriend(user.uid, friendUserName)
            result
        } catch (e: Exception) {
            Log.d("Failed Requested Friend - VM", "Failed Friend")
            Result.failure(e)
        }
    }
    suspend fun acceptFriend(friendUserName: String) {
        Log.d("Friends", "Accept Friend Called in UserVM")
        val user = _user.value ?: return
        viewModelScope.launch {
            userDao.acceptFriend(user.uid, friendUserName)
            _friends.value = userDao.getFriends(user.uid).getOrNull()
        }
    }
    suspend fun removeFriend(friendUserName: String) {
        val user = _user.value ?: return
        viewModelScope.launch {
            userDao.removeFriend(user.uid, friendUserName)
            _friends.value = userDao.getFriends(user.uid).getOrNull()
        }
    }

    private fun setFriendListener(userID: String) {
        _friendListener = userDao.getFriendsRef(userID)
            .addSnapshotListener{ snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) {
                    "Local"
                } else {
                    "Server"
                }

                if (snapshot != null && snapshot.isEmpty) {
                    Log.d(TAG, "$source data: null")
                } else {
                    Log.d(TAG, "$source friends number: ${snapshot!!.documents.size}")
                    val friendList = convertDocumentsToFriends(snapshot.documents)
                    _friends.value = friendList
                }
            }

    }
}