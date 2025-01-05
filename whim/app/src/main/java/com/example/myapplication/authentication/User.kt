package com.example.myapplication.authentication


data class User(
    val userID: String,
    val email: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    var hostRating: Double = 0.0,
    var countHostRating: Double = 0.0,
    var eventsHosted: Double = 0.0
)