package com.example.myapplication.authentication
import android.net.Uri
import com.google.firebase.auth.FirebaseUser

const val emailDomainUW = "@uwaterloo.ca"

data class UserProfile(val firebaseUser: FirebaseUser?, val userDoc: User?) {
    val userID: String? = firebaseUser?.uid
    var email: String? = firebaseUser?.email
    var emailVerified: Boolean? = firebaseUser?.isEmailVerified
    var emailVerifiedUW: Boolean? = if (firebaseUser == null) null else
        emailVerified!!.and(email!!.endsWith(emailDomainUW, ignoreCase = true))
    val photoUrl: Uri? = firebaseUser?.photoUrl
    val userName: String? = userDoc?.userName
    val firstName = userDoc?.firstName
    val lastName = userDoc?.lastName
    var hostRating: Double? = userDoc?.hostRating
    var countHostRating: Double? = userDoc?.countHostRating
    var eventsHosted: Double? = userDoc?.eventsHosted

    constructor(firebaseUser: FirebaseUser?,
                userDoc: User?,
                email: String? = null) : this(firebaseUser, userDoc) {
        this.email = email ?: this.email
    }
}