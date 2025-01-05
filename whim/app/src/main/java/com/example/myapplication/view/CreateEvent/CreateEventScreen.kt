package com.example.myapplication.view.CreateEvent

import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.Routes
import com.example.myapplication.util.GeocodeResultCallback
import com.example.myapplication.util.getCoordinatesFromAddress
import com.example.myapplication.view.EventViewModel
import com.example.myapplication.view.UserViewModel
import java.time.LocalDateTime
import java.time.Month

data class Event(
    var title: String = "",
    var description: String = "",
    var startDate: LocalDateTime = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0),
    var endDate: LocalDateTime = LocalDateTime.of(1900, Month.JANUARY, 1, 0, 0),
    var location: String = "",
    var eventType: String = "",
    var attendeeLimit: Int = 0,
    var address: String = "",
    var host: String = "",
    var imageUri: Uri = Uri.parse(""),
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CreateEventScreen(navController: NavController, eventViewModel: EventViewModel, userViewModel: UserViewModel) {

    val initialEvent = Event()
    val event = remember { mutableStateOf(initialEvent) }
    val mainHandler = Handler(Looper.getMainLooper())

    val onSubmit: () -> Unit = {
        getCoordinatesFromAddress(
            context = navController.context,
            address = event.value.address,
            object : GeocodeResultCallback {
                override fun onSuccess(latitude: Double, longitude: Double) {
                    val result = eventViewModel.createEvent(
                        title = event.value.title,
                        description = event.value.description,
                        startTime = event.value.startDate,
                        endTime = event.value.endDate,
                        eventType = event.value.eventType,
                        attendeeLimit = event.value.attendeeLimit,
                        address = event.value.address,
                        latitude = latitude,
                        longitude = longitude,
                        imageUri = event.value.imageUri,
                        host = userViewModel.userLiveData.value?.uid ?: throw Exception("UserID is null"),
                    )
                    mainHandler.post {
                        if (Result.success(result).isSuccess) {
                            Toast.makeText(navController.context, "Event Created", Toast.LENGTH_LONG).show()
                        }
                        navController.popBackStack()
                    }
                }

                override fun onError(errorMessage: String) {
                    mainHandler.post {
                        Toast.makeText(navController.context, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    val newNavController: NavHostController = rememberNavController()
    NavHost(navController = newNavController, startDestination = Routes.CREATE_EVENT + "/1") {
        composable(route = Routes.CREATE_EVENT + "/1") {
            CreateEvent1(event, newNavController)
        }
        composable(route = Routes.CREATE_EVENT + "/2") {
            CreateEvent2(event, onSubmit)
        }
    }
}
