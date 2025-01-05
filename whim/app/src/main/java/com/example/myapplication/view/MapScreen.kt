package com.example.myapplication.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.events.Event
import com.example.myapplication.navigation.Routes
import com.example.myapplication.util.location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDateTime
import kotlin.random.Random

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val HUE_ROYAL_BLUE = 249.21f

@Composable
fun MapScreen(EventViewModel: EventViewModel, navController: NavController) {
    var eventListState by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Observer for events
    val eventObserver = Observer<List<Event>> {
        eventListState = it
        isLoading = false
    }

    // Observe events from the ViewModel
    EventViewModel.events.observeForever(eventObserver)

    // Fetch events using the ViewModel
    LaunchedEffect(Unit) {
        EventViewModel.fetchEvents(location(WATERLOO_LAT, WATERLOO_LONG), 10000.0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
    ) {
        MapViewContainer(eventListState, navController, onMarkerClick = { event ->
            selectedEvent = event
        })
    }

    selectedEvent?.let { event ->
        navController.navigate(Routes.EVENT_DETAILS + "/${event.eventID}")
    }
}

@Composable
fun MapViewContainer(eventListState: List<Event>, navController: NavController, onMarkerClick: (Event) -> Unit) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val activity = context as ComponentActivity
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    DisposableEffect(mapView) {
        mapView.onCreate(Bundle())
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDestroy()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxWidth()) { mapView ->
            mapView.getMapAsync { googleMap ->
                googleMap.clear() // Clear all markers on the map

                googleMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(WATERLOO_LAT, WATERLOO_LONG))
                            .zoom(15f)
                            .build()
                    )
                )

                // Check for location permissions
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    googleMap.isMyLocationEnabled = true

                    // Get the user's current location and add a marker
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val userLocation = LatLng(it.latitude, it.longitude)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
                        }
                    }
                } else {
                    // Request location permissions if not granted
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }

                // Add markers for each event in eventListState
                val currentTime = LocalDateTime.now()
                eventListState.filter { it.endTime.isAfter(currentTime) }.forEach { event ->
                    val noiseLat = Random.nextDouble(-0.0005, 0.0005)
                    val noiseLng = Random.nextDouble(-0.0005, 0.0005)
                    val eventLocation = LatLng(event.latitude + noiseLat, event.longitude + noiseLng)
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(eventLocation)
                            .title(event.title)
                            .icon(BitmapDescriptorFactory.defaultMarker(HUE_ROYAL_BLUE))
                    ).apply {
                        this?.tag = event
                    }
                }

                // Set a marker click listener to show the event details
                googleMap.setOnMarkerClickListener { marker ->
                    val event = marker.tag as? Event
                    event?.let {
                        onMarkerClick(it)
                        true
                    } ?: false
                }
            }
        }
        IconButton(
            onClick = { navController.navigate(Routes.EVENTS) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(60.dp)
                .padding(end = 24.dp, bottom = 24.dp)
                .background(color = Color.White, shape = RoundedCornerShape(30.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.scroll),
                contentDescription = "Back",
                tint = Color.Unspecified
            )
        }
    }
}
