package com.example.myapplication.tests

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.myapplication.authentication.FriendProfile
import com.example.myapplication.authentication.User
import com.example.myapplication.authentication.UserProfile
import com.example.myapplication.authentication.UserRepository
import com.example.myapplication.events.EventRepository
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.example.myapplication.view.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay

class FriendsTestActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventRepository: EventRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        userRepository = UserRepository(firestore)
        userViewModel = UserViewModel(auth, userRepository)
        eventRepository = EventRepository()
        // Create the layout programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        lifecycleScope.launch {
            userRepository.requestFriend("DAwkVK6YtobNF3426ZmnBnosuUl1","imaan 2")
            delay(1000)
            userRepository.acceptFriend("8wleJdwsQlOnb6uTbuUr75Czq2E2", "kevfighter")
        }
    }

    private fun testUser() {
        lifecycleScope.launch {
//            val requester = User(
//                userID = "",
//                email = "kevin1689@gmail.com",
//                userName = "kevfighter",
//                firstName = "Kev",
//                lastName = "Weng"
//            )
//            val requesterPassword = "Test Friend Requester Password"
//            userViewModel.takeUserName(requester.userName)
//            val createResult = userViewModel.createUser(requester.email, requesterPassword, requester.userName, requester.firstName, requester.lastName)

//            eventRepository.reserveEvent("gxmOVWsKH2VnxEIWoVMnfOqRTlf2", "0znTIAeCCgzaAV2O4R6q")
            val list = userViewModel.getAttendees("0znTIAeCCgzaAV2O4R6q")
            println(list)
            val noneList = userViewModel.getAttendees("not exist event id")
            println(noneList)
        }
    }


    private fun testFriend() {
        val requester = User(
            userID = "",
            email = "test_friend_requester@gmail.com",
            userName = "Test Friend Requester UserName",
            firstName = "Test Friend Requester FirstName",
            lastName = "Test Friend Requester LastName"
        )
        val requesterPassword = "Test Friend Requester Password"

        val receiver = User(
            userID = "",
            email = "test_friend_receiver@gmail.com",
            userName = "Test Friend Request Receiver UserName",
            firstName = "Test Friend Request Receiver FirstName",
            lastName = "Test Friend Request Receiver LastName"
        )
        val receiverPassword = "Test Friend Request Receiver Password"

        val friendListObserver = Observer<List<FriendProfile>?> { friendList ->
            if (friendList?.size == 1) {
                val friend = friendList[0]
                if (friend.friendStatus == FriendProfile.CONFIRMED && friend.email == receiver.email
                    && friend.userName == receiver.userName && friend.firstName == receiver.firstName
                    && friend.lastName == receiver.lastName) {
                    println("Friend Request Receiver acceptFriend() success")
                }
                if (friend.friendStatus == FriendProfile.CONFIRMED && friend.email == requester.email
                    && friend.userName == requester.userName && friend.firstName == requester.firstName
                    && friend.lastName == requester.lastName) {
                    println("Friend Request Requester got acceptFriend() confirmation success")
                }
            }
        }
        userViewModel.friends.observe(this, friendListObserver)

        userViewModel.viewModelScope.launch {
            userViewModel.takeUserName(receiver.userName)
            userViewModel.createUser(receiver.email, receiverPassword, receiver.userName, receiver.firstName, receiver.lastName)
            delay(2000)
            userViewModel.signOut()
            userViewModel.takeUserName(requester.userName)
            val createResult = userViewModel.createUser(requester.email, requesterPassword, requester.userName, requester.firstName, requester.lastName)
            userViewModel.requestFriend(receiver.userName)
            delay(10000)
            userViewModel.signOut()
            delay(1000)
            val signInResult = userViewModel.signIn(receiver.email, receiverPassword)
            userViewModel.acceptFriend(requester.userName)
            delay(10000)
            userViewModel.signOut()
            userViewModel.signIn(requester.email, requesterPassword)
            delay(5000)
            userViewModel.deleteCurrentUser()
            delay(1000)
            userViewModel.signIn(receiver.email, receiverPassword)
            userViewModel.deleteCurrentUser()
        }
    }
}