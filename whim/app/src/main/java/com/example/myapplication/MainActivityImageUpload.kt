package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.authentication.UserRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import com.bumptech.glide.Glide
import com.example.myapplication.events.EventRepository
import com.example.myapplication.view.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivityImageUpload : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var eventRepository: EventRepository
    private lateinit var userRepository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var userViewModel: UserViewModel
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firestore and FirebaseStorage first
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        // Initialize auth and repositories
        auth = FirebaseAuth.getInstance()
        userRepository = UserRepository(firestore)
        eventRepository = EventRepository()

        // Initialize ViewModel
        userViewModel = UserViewModel(auth, userRepository)

        // Create the layout programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create the ImageView programmatically
        imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        // Create the button programmatically
        val buttonPickImage = Button(this).apply {
            text = "Upload Image"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        // Add the ImageView and button to the layout
        layout.addView(imageView)
        layout.addView(buttonPickImage)

        // Set the layout as the content view
        setContentView(layout)

        // Initialize the image picker launcher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
            Log.d("Image Pick", "Selected image URI: $uri")
            uri?.let { uploadImage(it) }
        }

        // Set the button's click listener
        buttonPickImage.setOnClickListener {
            pickImage()
        }

        // Fetch and display the image for a specific event (example event ID: "abc")
        lifecycleScope.launch {
            displayEventImage("abc")
        }
    }

    private fun pickImage() {
        pickImageLauncher.launch("image/*")
    }

    private fun uploadImage(uri: Uri) {
        lifecycleScope.launch {
            val result = eventRepository.uploadEventImage("abc", uri)
            if (result.isSuccess) {
                Log.d("Image Upload", "Image uploaded successfully")
                displayEventImage("abc")
            } else {
                Log.d("Image Upload", "Image upload failed", result.exceptionOrNull())
            }
        }
    }

    private suspend fun displayEventImage(eventId: String) {
        val imageUrl = eventRepository.retrieveEventImage(eventId).toString()
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
        } else {
            Log.d("Image Display", "No image URL found for event $eventId")
        }
    }
}
