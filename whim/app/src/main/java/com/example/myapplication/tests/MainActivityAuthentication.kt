package com.example.myapplication.tests

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.myapplication.authentication.UserProfile
import com.example.myapplication.authentication.UserRepository
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.view.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

class MainActivityAuthentication : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        userRepository = UserRepository(firestore)
        userViewModel = UserViewModel(auth, userRepository)
        // Create the layout programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create the button programmatically
        val buttonVerify = Button(this).apply {
            text = "Verifiy email"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        // Add the button to the layout
        layout.addView(buttonVerify)

        // Set the layout as the content view
        setContentView(layout)


        buttonVerify.setOnClickListener {
            userViewModel.viewModelScope.launch {
                userViewModel.isEmailVerified()
            }
        }
        testAuthentication()
    }


    private fun testAuthentication() {
        val firstName = "Test FirstName"
        val lastName = "Test LastName"
        val userName = "Test UserName"
        val email = "kevfighter1689@gmail.com"
        val password = "hello world"

        val updatedFirstName = "Test 2 FirstName"
        val updatedLastName = "Test 2 LastName"
        val updatedUserName = "Test 2 UserName"

        val userProfileObserver = Observer<UserProfile> { userProfile ->
            if ((userProfile.email == email) and (userProfile.userName == userName)
                and (userProfile.lastName == lastName) and (userProfile.firstName == firstName)) {
                println("Set User Profile success")
            }
            if (userProfile.email == email) {
                if (userProfile.emailVerified!!) {
                    println("Email Verification success")
                }
            }

            if ((userProfile.userName == updatedUserName) and (userProfile.lastName == updatedLastName)
                and (userProfile.firstName == updatedFirstName)) {
                println("Update User Profile success")
            }
        }
        val userObserver = Observer<FirebaseUser?> { user ->
            if (user != null) {
                println("User signed in")
            } else {
                println("No user signed in")
            }
        }

        userViewModel.userLiveData.observe(this, userObserver)
        userViewModel.userProfile.observe(this, userProfileObserver)

        userViewModel.viewModelScope.launch {
            userViewModel.takeUserName(userName)
            val emailAvailableResult = userViewModel.isEmailAvailable(email)
            if (emailAvailableResult.isSuccess) {
                println("Email available success")
            }
            val createUserResult = userViewModel.createUser(email, password, userName, firstName, lastName)
            if (createUserResult.isSuccess) {
                println("Create User success")
            } else {
                println(createUserResult.toString())
            }

            // GO Verify Email
            // userViewModel.sendVerificationEmail()
            // delay(60000)
            val emailNotAvailableResult = userViewModel.isEmailAvailable(email)
            if (emailNotAvailableResult.isFailure) {
                println("Email not available success")
            }
            val takeTakenUserNameResult = userViewModel.takeUserName(userName)
            if (takeTakenUserNameResult.isFailure) {
                println("Ensuring User Name Uniqueness Success")
            }
            userViewModel.takeUserName(updatedUserName)
            userViewModel.updateUserName(updatedUserName)
            userViewModel.updateFirstLastName(updatedFirstName, updatedLastName)
            delay(10000)
            userViewModel.deleteCurrentUser()

            /*
            val deferredSignInResult = async {
                userViewModel.signIn(email, password)
            }
            val signInResult = deferredSignInResult.await()
            if (signInResult.isSuccess) {
                println("Sign In User success")
            } else {
                println(signInResult.toString())
            }
            */
        }
    }
}