package com.example.myapplication.events
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.myapplication.events.helpers.convertDocumentsToEvents
import com.example.myapplication.events.helpers.getEventTime
import com.example.myapplication.events.helpers.checkOverAttendanceLimit
import com.example.myapplication.util.FirebaseManager
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.StorageReference

class EventRepository() : EventDao {
    // Singleton pattern so that instances are created from a single place
    private val db: FirebaseFirestore
        get() = FirebaseManager.firestoreInstance

    private val storage: FirebaseStorage
        get() = FirebaseManager.storageInstance


    private fun validateEvent(event: Event): List<String> {
        val errors = mutableListOf<String>()

        if (event.title.isBlank() || event.title.length > 64) {
            errors.add("Title must not be empty and must be less than 64 characters.")
        }
        if (event.description.isBlank() || event.description.length > 256) {
            errors.add("Description must not be empty and must be less than 256 characters.")
        }
        if (event.startTime.isAfter(event.endTime)) {
            errors.add("Start time must be before end time.")
        }
        if (event.attendeeLimit <= 1) {
            errors.add("Attendee limit must be greater than 1.")
        }
        if (event.eventType.isBlank()) {
            errors.add("Event type must not be empty.")
        }
        if (event.latitude.isNaN() || event.longitude.isNaN()) {
            errors.add("Coordinates must not be empty.")
        }

        return errors
    }

    override suspend fun createEvent(title: String,
                                     description: String,
                                     startTime: LocalDateTime,
                                     endTime: LocalDateTime,
                                     eventType: String,
                                     attendeeLimit: Int,
                                     address: String,
                                     latitude: Double,
                                     longitude: Double,
                                     host: String,
                                     imageUri: Uri?,
                             ): Result<Unit> {
        // TODO: Add back error handling
//        val errors = validateEvent(event)
//        if (errors.isNotEmpty()) {
//            return Result.failure(Exception(errors.joinToString("; ")))
//        }

        return try {
            val geoLocation = GeoLocation(latitude, longitude)
            val geohash = GeoFireUtils.getGeoHashForLocation(geoLocation)
            val startTimestamp = Timestamp(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()))
            val endTimestamp = Timestamp(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()))

            // Create a map of data to upload
            val eventData = mapOf(
                "title" to title,
                "description" to description,
                "startTime" to startTimestamp,
                "endTime" to endTimestamp,
                "eventType" to eventType,
                "attendeeLimit" to attendeeLimit,
                "address" to address,
                "latitude" to latitude,
                "longitude" to longitude,
                "host" to host,
                "geohash" to geohash,
                "attendeeCount" to 0
            )
            val eventRef = db.collection("events").add(eventData).await()
            if (imageUri != null) {
                uploadEventImage(eventRef.id, imageUri)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getEventsByProximity(latitude: Double, longitude: Double, proximity: Double): List<Event> {
        // Geoquery logic obtained from: https://firebase.google.com/docs/firestore/solutions/geoqueries#kotlin+ktx_2
        // Only return
        val center = GeoLocation(latitude, longitude)
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, proximity)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        val currentLocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
        val currentTime = Timestamp(Date.from(currentLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()))
        for (b in bounds) {
            val q = db.collection("events")
                .orderBy("geohash")
                .startAt(b.startHash)
                .endAt(b.endHash)
                .whereGreaterThan("endTime", currentTime)
            tasks.add(q.get())
        }

        val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()

        // Convert each task in list to a suspendable coroutine (QuerySnapshot)
        // Wait until all tasks are available before proceeding
        val results = tasks.map { it.await() }

        for (snap in results) {
            for (doc in snap.documents) {
                val lat = doc.getDouble("latitude")!!
                val lng = doc.getDouble("longitude")!!
                val docLocation = GeoLocation(lat, lng)
                val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                if (distanceInM <= proximity) {
                    matchingDocs.add(doc)
                }
            }
        }

        val eventsList = convertDocumentsToEvents(matchingDocs)
        println("events: got list $latitude, $longitude, $proximity, $eventsList")
        return eventsList
    }

    // active == true, indicates current-future events
    // active == false, indicates past events
    override suspend fun getEventsJoined(userID: String, active: Boolean): Result<List<Event>> {
        val eventsUsersQuery = db.collection("events_users").whereEqualTo("userId", userID)
        val currentLocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
        val currentTime = Timestamp(Date.from(currentLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()))

        val eventsRef = db.collection("events")
        val joinedEventsDocs: MutableList<DocumentSnapshot> = mutableListOf()

        try {
            // Get all documents from events_users collection for the given user
            val eventsUsersSnapshot = eventsUsersQuery.get().await()

            for (doc in eventsUsersSnapshot.documents) {
                val eventID = doc.getString("eventId") ?: continue
                var eventCheckOut = doc.get("check_out")
                if (eventCheckOut != null) {
                    eventCheckOut = eventCheckOut.toString().toBoolean()
                }
                val eventDoc = eventsRef.document(eventID).get().await()

                if (eventDoc.exists()) {
                    val eventEndTime = eventDoc.getTimestamp("endTime")

                    // Check if the event should be included based on the active flag and event end time
                    if (eventEndTime != null) {
                        if (active && eventEndTime > currentTime && eventCheckOut != true) {
                            joinedEventsDocs.add(eventDoc)
                        } else if (!active && (eventEndTime < currentTime || eventCheckOut == true)) {
                            joinedEventsDocs.add(eventDoc)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return Result.success(convertDocumentsToEvents(joinedEventsDocs))
    }


    // active == true, indicates current-future events
    // active == false, indicates past events
    override suspend fun getEventsHosted(userId: String, active: Boolean): Result<List<Event>> {
        var hostedEventsQuery = db.collection("events").whereEqualTo("host", userId)
        val currentLocalDateTime = LocalDateTime.now(ZoneId.systemDefault())
        val currentTime = Timestamp(Date.from(currentLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()))
        if (active) {
            hostedEventsQuery = hostedEventsQuery.whereGreaterThan("endTime", currentTime)
        } else {
            hostedEventsQuery = hostedEventsQuery.whereLessThan("endTime", currentTime)
        }

        try {
            val hostedEventsDoc = hostedEventsQuery.get().await().documents
            return Result.success(convertDocumentsToEvents(hostedEventsDoc))
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun uploadEventImage(eventId: String, uri: Uri): Result<Unit> {
        return try {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/event_images/${eventId}")

            val uploadTask = imageRef.putFile(uri).await() // Use await() to suspend until the task is complete
            Log.d("Image Upload Success", "File Name: ${uploadTask.metadata?.name}, File Size: ${uploadTask.metadata?.sizeBytes}")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("Image Upload Fail", "Failed upload", e)
            Result.failure(e)
        }
    }

    override suspend fun reserveEvent(userId: String, eventId: String): Result<Unit> {
        try {
            val eventEndTime = getEventTime(eventId, "endTime")
            Log.d("END TIME", eventEndTime.toString())
            // If event has not started
            if (eventEndTime != null && eventEndTime.isAfter(LocalDateTime.now())) {
                if(!checkOverAttendanceLimit(eventId)) {
                    val eventAttendee = mapOf(
                        "userId" to userId,
                        "eventId" to eventId,
                        "check_in" to false,
                        "check_out" to null
                    )
                    // Set attendee document in events_users collection
                    db.collection("events_users").document(userId + "_" + eventId).set(eventAttendee).await()
                    db.collection("events").document(eventId).update("attendee_count", FieldValue.increment(1)).await()
                    return Result.success(Unit)
                } else {
                    println("reserving event at capacity")
                    return Result.failure(IllegalStateException("Event is at attendance capacity, cannot join."))
                }
            } else {
                // Handle case where event end time is null or in the past
                println("reserving event after it has started, event end time $eventEndTime, now ${LocalDateTime.now()}")
                return Result.failure(IllegalStateException("Cannot reserve event after it has started"))
            }
        } catch (e: Exception) {
            println("reserving failed in repo: $e")
            // Handle any exceptions and return failure result
            return Result.failure(e)
        }
    }

    override suspend fun leaveEvent(userId: String, eventId: String): Result<Unit> {
        // add handling so that you can't leave event after it has started
        return try {
            val eventStartTime = getEventTime(eventId, "startTime")
            val currentTime = LocalDateTime.now()
            if(currentTime.isBefore(eventStartTime)) {
                db.collection("events_users").document(userId + "_" + eventId).delete().await()
                db.collection("events").document(eventId).update("attendee_count", FieldValue.increment(-1)).await()
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Cannot leave event after it has started"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkIntoEvent(userId: String, eventId: String): Result<Unit> {
        return try {
            val eventSignupRef = db.collection("events_users").document(userId + "_" + eventId)
            val eventSignup = eventSignupRef.get().await()
            if(eventSignup.exists()) {
                if(eventSignup.getBoolean("check_in") == false) {
                    val currentTime = LocalDateTime.now()
                    val eventStartTime = getEventTime(eventId, "startTime")
                    val eventEndTime = getEventTime(eventId, "endTime")
                    if(currentTime.isAfter(eventStartTime) && currentTime.isBefore(eventEndTime)) {
                        eventSignupRef.update("check_in", true).await()
                        Result.success(Unit)
                    } else {
                        Result.failure(IllegalStateException("Window for checking in is not open"))
                    }
                } else {
                    Result.failure(IllegalStateException("User already checked into event!"))
                }
            } else {
                Result.failure(IllegalStateException("Cannot check into event, user did not sign up for it"))
            }
        } catch(e: Exception) {
            Result.failure(e)
        }
        // 1. check if user has already signed up for event
        // 2. check if time is in range, if before throw error, if after throw error
        // 3. if passed the above fields, then update the check in field of events to true
    }
//
    override suspend fun checkOutOfEvent(userId: String, eventId: String): Result<Unit> {
    return try {
        val eventSignupRef = db.collection("events_users").document(userId + "_" + eventId)
        val eventSignup = eventSignupRef.get().await()
        if(eventSignup.exists()) {
            if(eventSignup.getBoolean("check_in") == true) {
                val currentTime = LocalDateTime.now()
                val eventStartTime = getEventTime(eventId, "startTime")
                if(currentTime.isAfter(eventStartTime)) {
                    eventSignupRef.update("check_out", true).await()
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalStateException("Window for checking in is not open"))
                }
            } else {
                Result.failure(IllegalStateException("User has not checked into event, cannot check out."))
            }
        } else {
            Result.failure(IllegalStateException("Cannot check out of event, user did not sign up for it"))
        }
    } catch(e: Exception) {
        Result.failure(e)
    }
    // 1. check if user has already signed up for event
    // 2. check if time is in range, if before throw error, if after throw error
    // 3. if passed the above fields, then update the check in field of events to true
    }
    override suspend fun updateEvent(eventId: String, field: String, value: Any): Result<Unit> {
        return try {
            val eventRef = db.collection("events").document(eventId)
            eventRef.update(field, value).await()
            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to Update Error", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            db.collection("events").document(eventId).delete().await()
            Result.success(Unit)
        } catch(e: Exception) {
            Log.e(TAG, "Failed to Delete Error", e)
            Result.failure(e)
        }
    }
    override suspend fun retrieveEventImage(eventId: String): Uri? {
        return try {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/event_images/$eventId")

            val imageUri = imageRef.downloadUrl.await()
            imageUri
        } catch (e: Exception) {
            Log.e(TAG, "Image Retrieval Fail", e)
            null
        }
    }
}
