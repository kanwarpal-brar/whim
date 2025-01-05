package com.example.myapplication.events.helpers
import android.util.Log
import com.example.myapplication.events.EventRepository
import com.example.myapplication.events.Event
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.AggregateSource
import com.example.myapplication.util.FirebaseManager
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

suspend fun convertDocumentsToEvents(documentSnapshots: MutableList<DocumentSnapshot>): List<Event> {
    val events: MutableList<Event> = mutableListOf()
    for (doc in documentSnapshots) {
        val eventID = doc.id
        val title = doc.getString("title") ?: ""
        val description = doc.getString("description") ?: ""
        val startTimeTimestamp = doc.getTimestamp("startTime")
        val endTimeTimestamp = doc.getTimestamp("endTime")
        val address = doc.getString("address") ?: ""
        val longitude = doc.getDouble("longitude")!!
        val latitude = doc.getDouble("latitude")!!
        val eventType = doc.getString("eventType") ?: ""
        val attendeeLimit = doc.getLong("attendeeLimit")?.toInt() ?: 0
        val host = doc.getString("host")?: ""
        var attendeeCount = doc.getLong("attendee_count")?.toInt() ?: 0
        if (attendeeCount < 0) { attendeeCount = 0 }

        val startTime = requireNotNull(startTimeTimestamp?.let { timestampToLocalDateTime(it) }) {
            "startTime must not be null"
        }

        val endTime = requireNotNull(endTimeTimestamp?.let { timestampToLocalDateTime(it) }) {
            "endTime must not be null"
        }

        // fetching event image from firebase storage
        val eventRepository = EventRepository()
        val imageUri = eventRepository.retrieveEventImage(eventID)

        val event = Event(
            eventID = eventID,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            address = address,
            longitude = longitude,
            latitude = latitude,
            eventType = eventType,
            attendeeLimit = attendeeLimit,
            imageUri = imageUri,
            host = host,
            attendeeCount = attendeeCount
        )
        events.add(event)
    }
    return events
}

fun timestampToLocalDateTime(timestamp: Timestamp): LocalDateTime {
    val instant = timestamp.toDate().toInstant()
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
}

suspend fun getEventTime(eventId: String, fieldName: String): LocalDateTime? {
    val db = FirebaseManager.firestoreInstance
    return try {
        val eventDocRef = db.collection("events").document(eventId)
        val documentSnapshot = eventDocRef.get().await()
        val timeStamp = documentSnapshot.getTimestamp(fieldName)
        println("reserving, got $fieldName value $timeStamp from event $eventId")
        timeStamp?.let { timestampToLocalDateTime(it) }
    } catch (e: Exception) {
        println("reserving, error fetching $fieldName from event $eventId ,$e")
        Log.e("getEventTime", "Error fetching $fieldName from event $eventId", e)
        null
    }
}

suspend fun getEventCapacity(eventId: String): Int? {
    val db = FirebaseManager.firestoreInstance
    return try {
        val eventDocRef = db.collection("events").document(eventId)
        val documentSnapshot = eventDocRef.get().await()
        val eventCapacity = documentSnapshot.getLong("attendeeLimit")?.toInt()
        eventCapacity
    } catch (e: Exception) {
        Log.e("getEventCapacity", "Error fetching capacity from event $eventId", e)
        null
    }
}
suspend fun getCurrentNumberAttendees(eventId: String): Int? {
    val db = FirebaseManager.firestoreInstance
    return try {
        val query = db.collection("events_users").whereEqualTo("eventId", eventId)
        val queryCount = query.count()
        val count = queryCount.get(AggregateSource.SERVER).await()
        count.count.toInt()
    } catch(e: Exception) {
        Log.e("getCurrentNumberAttendees", "Failed to get number attendees")
        null
    }
}

suspend fun checkOverAttendanceLimit(eventId: String): Boolean {
    val eventCapacity = getEventCapacity(eventId)
    val numberAttendees = getCurrentNumberAttendees(eventId)
    return if (numberAttendees != null) {
        numberAttendees == eventCapacity!!
    } else {
        true
    }
}