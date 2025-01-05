//package com.example.myapplication.unit.workers
//
//import android.content.Context
//import android.location.Location
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.example.myapplication.events.Event
//import com.example.myapplication.events.EventRepository
//import com.example.myapplication.workers.GeofenceUpdateWorker
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.GeofencingClient
//import com.google.android.gms.location.GeofencingRequest
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.tasks.Tasks
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.slot
//import kotlinx.coroutines.runBlocking
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.time.LocalDateTime
//
//@RunWith(AndroidJUnit4::class)
//class GeofenceUpdateWorkerTest {
//
//
//    private val mockEventRepository: EventRepository = mockk()
//    private val mockFusedLocationProviderClient = mockk<FusedLocationProviderClient>()
//    private val mockUserLocation = mockk<Location>()
//    private val mockGeofencingClient = mockk<GeofencingClient>()
//    private var sampleEvents = mutableListOf<Event>()
//    private lateinit var context: Context
//    private lateinit var worker: GeofenceUpdateWorker
//
//    @Before
//    fun setup() {
//        // Application Context
//        context = ApplicationProvider.getApplicationContext()
//
//        // Set up GeofencingClient
//        every { LocationServices.getGeofencingClient(context) } returns mockGeofencingClient
//
//        // Set up mock behaviour to provide mock location
//        every { LocationServices.getFusedLocationProviderClient(context) } returns mockFusedLocationProviderClient
//        val mockLocationTask = Tasks.forResult(mockUserLocation)
//        coEvery { mockFusedLocationProviderClient.lastLocation } returns mockLocationTask
//        every { mockUserLocation.latitude } returns 43.47065961386296
//        every { mockUserLocation.longitude } returns -80.5283565759445
//
//        // Create sampleEvents array
//        sampleEvents = arrayListOf(
//            Event(
//                eventID = "eventOne",
//                title = "Event One",
//                description = "This is a test event",
//                startTime = LocalDateTime.now(),
//                endTime = LocalDateTime.now().plusHours(2),
//                latitude= 43.47,
//                longitude= -80.53,
//                eventType = "Test",
//                attendeeLimit = 5),
//            Event(
//                eventID = "eventTwo",
//                title = "Event Two",
//                description = "This is a test event",
//                startTime = LocalDateTime.now().minusHours(1),
//                endTime = LocalDateTime.now().plusHours(2),
//                latitude= 43.4695,
//                longitude= -80.5290,
//                eventType = "Test",
//                attendeeLimit = 2))
//        coEvery { mockEventRepository.getEventsByProximity(any(), any(), any()) } returns sampleEvents
//
//        // Create worker
//        worker = GeofenceUpdateWorker(context, mockk())
//    }
//
//    @Test
//    fun successfullyGetEventsAndCreateGeofences() = runBlocking {
//        val geofencingRequestSlot = slot<GeofencingRequest>()
//        worker.doWork()
//
//        coVerify {
//            mockGeofencingClient.addGeofences(capture(geofencingRequestSlot), any())
//        }
//        val capturedRequest = geofencingRequestSlot.captured
//
//        assert(capturedRequest.geofences.size == 2)
//    }
//}
