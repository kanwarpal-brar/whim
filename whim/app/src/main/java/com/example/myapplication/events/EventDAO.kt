package com.example.myapplication.events

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.LocalDateTime
// interface for interacting with database
interface EventDao {
    suspend fun createEvent(title: String,
                    description: String,
                    startTime: LocalDateTime,
                    endTime: LocalDateTime,
                    eventType: String,
                    attendeeLimit: Int,
                    address: String,
                    latitude: Double,
                    longitude: Double,
                    host: String,
                    imageUri: Uri? = null,): Result<Unit>
    suspend fun getEventsByProximity(latitude: Double,
                                     longitude: Double,
                                     proximity: Double): List<Event>
    suspend fun getEventsJoined(userID: String, active: Boolean): Result<List<Event>>
    suspend fun getEventsHosted(userId: String, active: Boolean): Result<List<Event>>
    suspend fun updateEvent(eventId: String, field: String, value: Any): Result<Unit>
    suspend fun deleteEvent(eventId: String): Result<Unit>
    suspend fun uploadEventImage(eventId: String, uri: Uri): Result<Unit>
    suspend fun reserveEvent(userId: String, eventId: String): Result<Unit>

    suspend fun leaveEvent(userId: String, eventId: String): Result<Unit>

    suspend fun checkIntoEvent(userId: String, eventId: String): Result<Unit>

    suspend fun checkOutOfEvent(userId: String, eventId: String): Result<Unit>
    suspend fun retrieveEventImage(eventId: String): Uri?
}