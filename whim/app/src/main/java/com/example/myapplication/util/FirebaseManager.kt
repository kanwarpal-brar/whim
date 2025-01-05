package com.example.myapplication.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

// FirebaseManager.kt
object FirebaseManager {
    val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
}
