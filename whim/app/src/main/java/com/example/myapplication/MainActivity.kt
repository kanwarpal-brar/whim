package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Navigation
import com.example.myapplication.ui.theme.AppTheme
import com.example.myapplication.view.NavBar
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplication.authentication.UserRepository
import com.example.myapplication.events.EventRepository
import com.example.myapplication.navigation.Routes
import com.example.myapplication.navigation.USER_AUTH_ROUTES
import com.example.myapplication.util.location
import com.example.myapplication.view.EventViewModel
import com.example.myapplication.view.UserViewModel
import com.example.myapplication.workers.GeofenceUpdateWorker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

val WATERLOO_LAT = 43.4643
val WATERLOO_LONG = -80.5204

class MainActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var eventRepository: EventRepository
    private lateinit var userRepository: UserRepository
    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(eventRepository, location(WATERLOO_LAT, WATERLOO_LONG))
    }

    private lateinit var userViewModel: UserViewModel

    private lateinit var geoFenceWorkerRequest: PeriodicWorkRequest

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        eventRepository = EventRepository()
        userRepository = UserRepository(firestore)
        userViewModel = UserViewModel(auth, userRepository)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (
                            !USER_AUTH_ROUTES.contains(currentRoute) &&
                            currentRoute?.contains(Routes.EVENT_DETAILS) == false &&
                            !currentRoute.contains(Routes.EVENT_PARTICIPANTS) &&
                            !currentRoute.contains(Routes.EVENT_CHECKOUT)
                        ) {
                            BottomAppBar(
                                containerColor = Color.Transparent,
                                content = {
                                    NavBar(navController = navController)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Navigation(navController, userViewModel, innerPadding, eventViewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Create Periodic Work Request for geofencing
        geoFenceWorkerRequest = PeriodicWorkRequestBuilder<GeofenceUpdateWorker>(
            15, TimeUnit.MINUTES,
        )
            .build()
        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "PeriodicGeofenceUpdateWorker",
                ExistingPeriodicWorkPolicy.UPDATE,
                geoFenceWorkerRequest)
    }

    private fun createEvents() {
        lifecycleScope.launch {
            userViewModel.signIn("jamnetworkuw@gmail.com", "password")

            val userID = userViewModel.userLiveData.value?.uid ?: ""
            var startTimeString = "01-08-2024 10:46:26"
            var endTimeString = "02-08-2024 08:45:00"
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            var startTime = LocalDateTime.parse(startTimeString, formatter)
            var endTime = LocalDateTime.parse(endTimeString, formatter)

            eventViewModel.createEvent(
                title = "Community Camp Fire",
                description = "Join us at CLV. For campfire and s'mores",
                startTime = startTime,
                endTime = endTime,
                eventType = "Social",
                attendeeLimit = 10,
                address = "CLV",
                latitude = 43.47286648577836,
                longitude = -80.56282014907713,
                host = userID,
                imageUri = null
            )
        }
    }

    private fun createTestEvent() {
        val startTimeString = "21-06-2024 15:30:00"
        val endTimeString = "21-06-2024 18:30:00"
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val startTime = LocalDateTime.parse(startTimeString, formatter)
        val endTime = LocalDateTime.parse(endTimeString, formatter)


        lifecycleScope.launch {
            val result = eventRepository.createEvent(
                title = "Test Event",
                description = "This is a test event",
                startTime = startTime,
                endTime = endTime,
                eventType = "Test",
                attendeeLimit = 5,
                latitude = 43.473265,
                longitude = -80.539801,
                address = "University of Waterloo",
                host = "me")
            if (result.isSuccess) {
                // Handle success (e.g., show a success message)
                println("Event created successfully")
            } else {
                // Handle error (e.g., show an error message)
                println("Error creating event: ${result.exceptionOrNull()}")
            }
        }
    }

    private fun getEventsByProximity() {
        val latitude = 43.473265
        val longitude = -80.539801
        val proximity = 1000.0 // proximity in meters

        lifecycleScope.launch {
            val events = eventRepository.getEventsByProximity(latitude, longitude, proximity)
            // Display the list of events
            val eventNames = events.joinToString("\n") { it.title }
            Toast.makeText(this@MainActivity, eventNames, Toast.LENGTH_LONG).show()
        }
    }

//    fun testUserRepository() {
//        val userRepository = UserRepository(firestore)
//        val userID = "5RT1XwtnS8WD2J2Q5Wq4RCU13oJ3"
//        val userEmail = "test@gmail.com"
//        val userName = "Test User Name"
//        val userFirstName = "Test First Name"
//        val userLastName = "Test Last Name"
//        val user = User(
//            userID = userID,
//            email = userEmail,
//            userName = userName,
//            firstName = userFirstName,
//            lastName = userLastName,
//        )
//        val updatedUserEmail = "test2@gmail.com"
//        val updateUserName = "Test 2 User Name"
//        val updatedUserFirstName = "Test 2 First Name"
//        val updatedUserLastName = "Test 2 Last Name"
//        val updatedUser = User(
//            userID = userID,
//            email = updatedUserEmail,
//            userName = updateUserName ,
//            firstName = updatedUserFirstName,
//            lastName = updatedUserLastName,
//        )
//
//        lifecycleScope.launch {
//            val createUserResult = userRepository.createUser(
//                userID = userID,
//                email = userEmail,
//                userName = userName,
//                firstName = userFirstName,
//                lastName = userLastName)
//            if (createUserResult.isSuccess) {
//                println("Create User Doc in firestore success")
//            }
//            val storedUser = userRepository.getUser(userID)
//            if (storedUser == user)  {
//                println("Get User Doc success")
//            }
//            val updateUserResult = userRepository.updateUser(
//                userID = userID,
//                email = updatedUserEmail,
//                userName = updateUserName,
//                firstName = updatedUserFirstName,
//                lastName = updatedUserLastName)
//            if (updateUserResult.isSuccess) {
//                val updatedStoredUser = userRepository.getUser(userID)
//                if (updatedStoredUser == updatedUser) {
//                    println("Update User Doc success")
//                }
//            }
//            val deleteUserResult = userRepository.deleteUser(userID)
//            if (deleteUserResult.isSuccess) {
//                println("Delete User Doc success")
//            }
//        }
//    }
}

@Composable
fun MainScreen(onCreateEvent: () -> Unit, onGetEvents: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { onCreateEvent() }) {
            Text("Create Event")
        }
        Spacer(modifier = Modifier.padding(vertical = 8.dp))
        Button(onClick = { onGetEvents() }) {
            Text("Get Events By Proximity")
        }
    }
}

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text("Filled")
    }
}

@Composable
@Preview
fun PreviewMainScreen() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen({}) {}
        }
    }
}