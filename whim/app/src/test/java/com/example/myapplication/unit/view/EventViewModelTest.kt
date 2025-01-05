package com.example.myapplication.unit.view

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.myapplication.events.AnonymousEvent
import com.example.myapplication.events.Event
import com.example.myapplication.events.EventRepository
import com.example.myapplication.view.DEFAULT_EVENT_FETCH_PROXIMITY
import com.example.myapplication.view.EventViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID

class EventViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: EventViewModel
    private lateinit var eventRepository: EventRepository
    private lateinit var userLocation: Location

    private var sampleEvents: MutableList<Event> = mutableListOf()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        sampleEvents = arrayListOf(
            Event(
                eventID = "eventOne",
                title = "Event One",
                description = "This is a test event",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(2),
                latitude= 43.47,
                longitude= -80.53,
                eventType = "Test",
                attendeeLimit = 5,
                address = "University of Waterloo",
                host = "me"),
            Event(
                eventID = "eventTwo",
                title = "Event Two",
                description = "This is a test event",
                startTime = LocalDateTime.now().minusHours(1),
                endTime = LocalDateTime.now().plusHours(2),
                latitude= 43.4695,
                longitude= -80.5290,
                eventType = "Test",
                attendeeLimit = 2,
                address = "University of Waterloo",
                host = "me"))
        eventRepository = mockk()
        coEvery { eventRepository.getEventsByProximity(any(), any(), any()) } returns sampleEvents
        coEvery { eventRepository
            .createEvent(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
            .answers {
                val event = Event(
                    eventID = UUID.randomUUID().toString(),
                    title = it.invocation.args[0] as String,
                    description
                    = it.invocation.args[1] as String,
                    startTime = it.invocation.args[2] as LocalDateTime,
                    endTime = it.invocation.args[3] as LocalDateTime,
                    eventType = it.invocation.args[4] as String,
                    attendeeLimit = it.invocation.args[5] as Int,
                    latitude = it.invocation.args[6] as Double,
                    longitude = it.invocation.args[7] as Double,
                    address = it.invocation.args[8] as String,
                    host = it.invocation.args[9] as String)
                sampleEvents.add(event)
                Result.success(Unit) }
        userLocation = mockk()
        every { userLocation.latitude } returns 43.47065961386296
        every { userLocation.longitude } returns -80.5283565759445
        viewModel = EventViewModel(eventRepository, userLocation)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch events and update LiveData`() = runBlocking {
        viewModel = EventViewModel(eventRepository, userLocation)

        val observer = mockk<Observer<List<Event>>>(relaxed = true)
        viewModel.events.observeForever(observer)

        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)
    }

    @Test
    fun `fetchEvents sh        viewModel = EventViewModel(eventRepository, userLocation)

    val observer = mockk<Observer<List<Event>>>(relaxed = true)
    viewModel.events.observeForever(observer)ould fetch events and update LiveData`() = runBlocking {


        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)

        sampleEvents.add(
            Event(
                eventID = "newEvent",
                title = "New Event",
                description = "This is a test event",
                startTime = LocalDateTime.now().plusHours(1),
                endTime = LocalDateTime.now().plusHours(2),
                latitude= 43.47,
                longitude= -80.53,
                eventType = "Test",
                attendeeLimit = 4,
                address = "University of Waterloo",
                host = "me")
        )

        viewModel.fetchEvents(userLocation, DEFAULT_EVENT_FETCH_PROXIMITY)
        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)
    }

    @Test
    fun `createEvents should update events and update LiveData`() = runBlocking {
        viewModel = EventViewModel(eventRepository, userLocation)

        val observer = mockk<Observer<List<Event>>>(relaxed = true)
        viewModel.events.observeForever(observer)

        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)

        viewModel.createEvent(
            title = "New Event",
            description = "This is a test event",
            startTime = LocalDateTime.now().plusHours(1),
            endTime = LocalDateTime.now().plusHours(2),
            latitude= 43.47,
            longitude= -80.53,
            eventType = "Test",
            attendeeLimit = 4,
            address = "University of Waterloo",
            imageUri = null,
            host = "me")

        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)

        viewModel.createEvent(
            AnonymousEvent(
                title = "Other New Event",
                description = "This is a test event",
                startTime = LocalDateTime.now().plusHours(1),
                endTime = LocalDateTime.now().plusHours(2),
                latitude= 43.47,
                longitude= -80.53,
                eventType = "Test",
                attendeeLimit = 4,
                address = "University of Waterloo",
                host = "me"
            ),
            imageUri = null
        )

        coVerify { observer.onChanged(sampleEvents) }
        assertEquals(sampleEvents, viewModel.events.value)
    }

}