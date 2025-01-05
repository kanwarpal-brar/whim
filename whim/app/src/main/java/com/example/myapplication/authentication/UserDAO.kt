package com.example.myapplication.authentication

import com.google.firebase.firestore.CollectionReference


// interface for interacting with database
interface UserDAO {
    suspend fun takeUserName(userName: String): Result<Unit>
    suspend fun isEmailAvailable(email: String): Result<Unit>
    suspend fun createUser(userID: String,
                           email: String,
                           userName: String,
                           firstName: String,
                           lastName: String): Result<Unit>
    suspend fun deleteUser(userID: String): Result<Unit>
    suspend fun getUser(userID: String): Result<User>
    suspend fun updateUser(userID: String,
                           email: String? = null,
                           userName: String? = null,
                           firstName: String? = null,
                           lastName: String? = null): Result<Unit>
    suspend fun getAttendees(eventID: String): Result<List<User>>
    suspend fun getAttendanceStatus(eventID: String, userID: String): Result<AttendanceStatus>
    suspend fun requestFriend(userID: String, friendUserName: String): Result<Unit>
    suspend fun acceptFriend(userID: String, friendUserName: String): Result<Unit>
    suspend fun removeFriend(userID: String, friendUserName: String): Result<Unit>
    suspend fun getFriends(userID: String): Result<List<FriendProfile>>
    suspend fun submitHostRating(userID: String, rating: Double): Result<Unit>
    fun getFriendsRef(userID: String): CollectionReference
}