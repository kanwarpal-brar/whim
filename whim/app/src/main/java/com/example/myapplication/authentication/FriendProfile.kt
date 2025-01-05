package com.example.myapplication.authentication

const val PENDING = "Pending"
const val ACCEPTED = "Accepted"
data class FriendProfile(
    val userID: String,
    val userName: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val friendStatus: String,
) {
    companion object {
        const val PENDING_REQUEST = "Pending Request"
        const val PENDING_ANSWER = "Pending Answer"
        const val CONFIRMED = "Confirmed"
    }
}