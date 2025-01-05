package com.example.myapplication.authentication.helpers

import com.example.myapplication.authentication.FriendProfile
import com.example.myapplication.authentication.User
import com.google.firebase.firestore.DocumentSnapshot

fun convertDocumentToUser(doc: DocumentSnapshot): User {
    val userID = doc.id
    val email = doc.get("email").toString()
    val userName = doc.get("userName").toString()
    val firstName = doc.get("firstName").toString()
    val lastName = doc.get("lastName").toString()
    val hostRating = doc.get("hostRating")?.toString()?.toDouble() ?: 0.0
    val countHostRating = doc.get("countHostRating")?.toString()?.toDouble() ?: 0.0
    val eventsHosted = doc.get("eventsHosted")?.toString()?.toDouble() ?: 0.0

    val user = User(
        userID= userID,
        email = email,
        userName = userName,
        firstName = firstName,
        lastName = lastName,
        hostRating = hostRating,
        countHostRating = countHostRating,
        eventsHosted = eventsHosted
    )
    return user
}

fun convertDocumentsToFriends(documentSnapshots: MutableList<DocumentSnapshot>): List<FriendProfile> {
    val friends: MutableList<FriendProfile> = mutableListOf()
    for (doc in documentSnapshots) {
        val userName = doc.get("userName").toString()
        val email = doc.get("email").toString()
        val firstName = doc.get("firstName").toString()
        val lastName = doc.get("lastName").toString()
        val friendStatus = doc.get("friendStatus").toString()

        val friend = FriendProfile(
            userID = doc.id,
            userName = userName,
            email = email,
            firstName = firstName,
            lastName = lastName,
            friendStatus = friendStatus
        )
        friends.add(friend)
    }
    return friends
}

fun convertDocumentsToIDs(documentSnapshots: MutableList<DocumentSnapshot>): List<String> {
    val userIDs: MutableList<String> = mutableListOf()
    for (doc in documentSnapshots) {
        val userID = doc.get("userId").toString()
        userIDs.add(userID)
    }
    return userIDs
}

fun createFriendUpdate(user: User, status: String): Map<String, String> {
    val update = mapOf(
        "userName" to user.userName,
        "email" to user.email,
        "firstName" to user.firstName,
        "lastName" to user.lastName,
        "friendStatus" to status,
    )
    return update
}