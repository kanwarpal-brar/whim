package com.example.myapplication.tests

import android.location.Location
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.myapplication.authentication.AttendanceStatus
import com.example.myapplication.authentication.User
import com.example.myapplication.authentication.UserRepository
import com.example.myapplication.events.EventRepository
import com.example.myapplication.view.EventViewModel
import com.example.myapplication.view.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

class AttendanceTestActivity : AppCompatActivity() {
    fun location(latitude: Double, longitude: Double): Location {
        val location = Location("dummyProvider")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel
    private lateinit var eventRepository: EventRepository
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(eventRepository, location(50.1, 50.1))
    }
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
        testAttendeeCount()
    }

    private fun testAttendeeCount() {
        lifecycleScope.launch {
            userViewModel.signIn("kevin1689@gmail.com", "helloworld")
            eventViewModel.reserveEvent("6HB7qM7CIGHvwgBM7wvD", "DAwkVK6YtobNF3426ZmnBnosuUl1")
        }
    }

    private fun testEventsHosted() {
        val eventsHostedTest1 = "Testing Events Hosted1"
        lifecycleScope.launch {
            userViewModel.signIn("kevin1689@gmail.com", "helloworld")
            val userID = userViewModel.userLiveData.value?.uid ?: throw Exception("UserID is null")
//            eventViewModel.createEvent(
//                title = eventsHostedTest1,
//                description = eventsHostedTest1,
//                startTime = LocalDateTime.now(ZoneId.systemDefault()),
//                endTime = LocalDateTime.now(ZoneId.systemDefault()).plusHours(2),
//                eventType = "",
//                attendeeLimit = 10,
//                address =  "",
//                latitude =  50.1,
//                longitude =  50.1,
//                host = userViewModel.userLiveData.value?.uid ?: "",
//                imageUri = null
//            )
            val eventsHostedListPast = eventViewModel.getEventsJoined(userID, active = false)
            println(eventsHostedListPast)
            val eventsHostedListCurrent = eventViewModel.getEventsJoined(userID, active = true)
            println(eventsHostedListCurrent)
        }
    }

    private fun testEventsJoined() {
        // TO-DO: write test set up


        // temp: tested with a userID currently reserved 2 events
        val userID_Joined2 = "8wleJdwsQlOnb6uTbuUr75Czq2E2"
        // temp: tested with userID who currently has not reserved any events
        val userID_Joined0 = "gxmOVWsKH2VnxEIWoVMnfOqRTlf2"

        eventViewModel.viewModelScope.launch {
            val list2Events = eventViewModel.getEventsJoined(userID_Joined2).getOrNull()
            if (list2Events!!.size == 2) {
                println("Success 2 events")
                println(list2Events)
            }

            val list0Events = eventViewModel.getEventsJoined(userID_Joined0).getOrNull()
            if (list0Events!!.size == 0) {
                println("Success 0 events")
                println(list0Events)
            }
        }
    }

    private fun testAttendance() {
        // create 4 users
        val testAttendancePassword = "test attendance pass"
        val testAttendance1 = "test_Attendance_1"
        val user1 = User(
            userID = "",
            email = "${testAttendance1}@gmail.com",
            userName = "$testAttendance1 UserName",
            firstName = "$testAttendance1 FirstName",
            lastName = "$testAttendance1  LastName"
        )
        val testAttendance2 = "test_Attendance_2"
        val user2 = User(
            userID = "",
            email = "${testAttendance2}@gmail.com",
            userName = "$testAttendance2 UserName",
            firstName = "$testAttendance2 FirstName",
            lastName = "$testAttendance2  LastName"
        )
        val testAttendance3 = "test_Attendance_3"
        val user3 = User(
            userID = "",
            email = "${testAttendance3}@gmail.com",
            userName = "$testAttendance3 UserName",
            firstName = "$testAttendance3 FirstName",
            lastName = "$testAttendance3  LastName"
        )
        val testAttendance4 = "test_Attendance_4"
        val user4 = User(
            userID = "",
            email = "${testAttendance4}@gmail.com",
            userName = "$testAttendance4 UserName",
            firstName = "$testAttendance4 FirstName",
            lastName = "$testAttendance4  LastName"
        )
        lifecycleScope.launch {
            // create test event
            eventRepository.createEvent(
                title = testAttendance1,
                description = testAttendance1,
                startTime = LocalDateTime.now(ZoneId.systemDefault()),
                endTime = LocalDateTime.now(ZoneId.systemDefault()).plusHours(2),
                eventType = "",
                attendeeLimit = 10,
                address =  "",
                latitude =  43.473265,
                longitude =  -80.539801,
                host = "Quel Con",
                imageUri = null
            )

            // TO-DO: redo fetching of eventID so that we get an event of with the valid start/end time
            val query = firestore.collection("events").whereEqualTo("title", testAttendance1)
            val snapshot = query.get().await()
            val eventID = snapshot.documents[0].id

            userViewModel.takeUserName(user1.userName)
            val createUserResult = userViewModel.createUser(email = user1.email, password = testAttendancePassword,
                userName = user1.userName, firstName = user1.firstName, lastName = user1.lastName)
            println("$createUserResult")
            val userID1 = userViewModel.userLiveData.value!!.uid
            delay(1000)
            userViewModel.signOut()
            delay(1000)

            userViewModel.takeUserName(user2.userName)
            userViewModel.createUser(email = user2.email, password = testAttendancePassword,
                userName = user2.userName, firstName = user2.firstName, lastName = user2.lastName)
            val userID2 = userViewModel.userLiveData.value!!.uid
            eventRepository.reserveEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            delay(1000)
            userViewModel.signOut()
            delay(1000)

            userViewModel.takeUserName(user3.userName)
            userViewModel.createUser(email = user3.email, password = testAttendancePassword,
                userName = user3.userName, firstName = user3.firstName, lastName = user3.lastName)
            val userID3 = userViewModel.userLiveData.value!!.uid
            eventRepository.reserveEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            eventRepository.checkIntoEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            delay(1000)
            userViewModel.signOut()
            delay(1000)

            userViewModel.takeUserName(user4.userName)
            userViewModel.createUser(email = user4.email, password = testAttendancePassword,
                userName = user4.userName, firstName = user4.firstName, lastName = user4.lastName)
            val userID4 = userViewModel.userLiveData.value!!.uid
            eventRepository.reserveEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            eventRepository.checkIntoEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            eventRepository.checkOutOfEvent(userId = userViewModel.userLiveData.value!!.uid,
                eventId = eventID)
            delay(1000)
            userViewModel.signOut()
            delay(1000)

            // User not reserved
            if (AttendanceStatus.NOT_RESERVED == userViewModel.getAttendanceStatus(eventID = eventID, userID = userID1).getOrNull()) {
                println("Not Reserved Status success")
            }
            // User reserved
            if (AttendanceStatus.RESERVED == userViewModel.getAttendanceStatus(eventID = eventID, userID = userID2).getOrNull()) {
                println("Reserved Status success")
            }
            // User checked-in
            if (AttendanceStatus.CHECKED_IN == userViewModel.getAttendanceStatus(eventID = eventID, userID = userID3).getOrNull()) {
                println("Checked-in Status success")
            }
            // User checked-out
            if (AttendanceStatus.CHECKED_OUT == userViewModel.getAttendanceStatus(eventID = eventID, userID = userID4).getOrNull()) {
                println("Checked-out Status success")
            }
        }
    }
}