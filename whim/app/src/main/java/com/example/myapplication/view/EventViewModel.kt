package com.example.myapplication.view

import android.location.Location
import android.location.LocationRequest
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.events.AnonymousEvent
import com.example.myapplication.events.Event
import com.example.myapplication.events.EventDao
import kotlinx.coroutines.launch
import java.time.LocalDateTime

const val DEFAULT_EVENT_FETCH_PROXIMITY = 600.0 // TODO: unify this in a constants file or ENV

class EventViewModel(private val eventDao: EventDao, userLocation: Location) : ViewModel() {
    companion object {
        const val CREATE_EVENT_AT_CURRENT_LOCATION_ADDRESS = "Current Location"
    }
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events
    private var lastUserLocation = userLocation

    init {
        viewModelScope.launch {
            _events.value = eventDao.getEventsByProximity(
                userLocation.latitude,
                userLocation.longitude,
                DEFAULT_EVENT_FETCH_PROXIMITY)
        }
    }

    fun fetchEvents(
        userLocation: Location,
        fetchProximity: Double = DEFAULT_EVENT_FETCH_PROXIMITY) {
        lastUserLocation = userLocation
        // TODO: figure out if viewing events and updating at same time causes a race condition
        viewModelScope.launch {
            val postVals = eventDao.getEventsByProximity(
                userLocation.latitude,
                userLocation.longitude,
                fetchProximity)
            _events.postValue(postVals)
        }
    }

    // DEFAULT: active == true, indicates current-future events
    // active == false, indicates past events
    suspend fun getEventsJoined(userId: String, active: Boolean = true): Result<List<Event>> {
        return eventDao.getEventsJoined(userId, active)
    }

    // DEFAULT: active == true, indicates current-future events
    // active == false, indicates past events
    suspend fun getEventsHosted(userId: String, active: Boolean = true): Result<List<Event>> {
        return eventDao.getEventsHosted(userId, active)
    }

    fun createEvent(event: AnonymousEvent) {
        createEvent(
            title = event.title,
            description = event.description,
            startTime = event.startTime,
            endTime = event.endTime,
            eventType = event.eventType,
            attendeeLimit = event.attendeeLimit,
            address = event.address,
            latitude = event.latitude,
            longitude = event.longitude,
            host = event.host,
            imageUri = event.imageUri
        )
    }

    fun createEvent(
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        eventType: String,
        attendeeLimit: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        imageUri: Uri?,
        host: String,
    ) {
        var lat = latitude
        var long = longitude
        var addr = address
        if (address == CREATE_EVENT_AT_CURRENT_LOCATION_ADDRESS) {
            // lastUserLocation is tracked on fetch; fetch is called often enough that this is
            //  a good approximation of current location
            lat = lastUserLocation.latitude
            long = lastUserLocation.longitude
            addr = "Custom Location"
        }
        viewModelScope.launch {
            eventDao.createEvent(
                title,
                description,
                startTime,
                endTime,
                eventType,
                attendeeLimit,
                addr,
                lat,
                long,
                host,
                imageUri
            )
            fetchEvents(lastUserLocation) // fetch events after creation
        }
    }

    fun reserveEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            eventDao.reserveEvent(userId = userId, eventId = eventId)
            fetchEvents(lastUserLocation)
        }
    }
    fun leaveEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            eventDao.leaveEvent(userId = userId, eventId = eventId)
            fetchEvents(lastUserLocation)
        }
    }
    fun checkIntoEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            eventDao.checkIntoEvent(userId = userId, eventId = eventId)
            fetchEvents(lastUserLocation)
        }
    }
    fun checkOutOfEvent(eventId: String, userId: String) {
        viewModelScope.launch {
            eventDao.checkOutOfEvent(userId = userId, eventId = eventId)
            fetchEvents(lastUserLocation)
        }
    }

    class EventViewModelFactory(
            private val eventDao: EventDao,
            private val location: Location
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
                return EventViewModel(eventDao, location) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}