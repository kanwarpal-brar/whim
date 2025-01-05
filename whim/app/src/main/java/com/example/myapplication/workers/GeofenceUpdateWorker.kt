package com.example.myapplication.workers

import android.location.Location
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapplication.events.Event
import com.example.myapplication.events.EventRepository
import com.example.myapplication.receivers.GeofenceBroadcastReceiver
import android.content.ContentValues.TAG
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await



class GeofenceUpdateWorker(
    private var appContext: Context,
    workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
        companion object {
            const val MAX_GEOFENCE = 100 // TODO: group events to reduce number of geofences
            const val DEFAULT_EVENT_RADIUS = 1500f // Events geofence in a radius of 1.5km
            const val DEFAULT_EVENT_FETCH_PROXIMITY = 3000.0 // Fetch all events within 3km
            const val FENCE_LOITER_DELAY = 60000 * 2 // Notify if within geofence for 2 minutes TODO: revert
            const val FENCE_EXPIRATION = 22L * 60000L // Delete geofences after 22 minutes
            const val GEOFENCE_INTENT = "ACTION_GEOFENCE_EVENT"
    }

    private var userLocation: Location? = null
    // Register the intent and broadcast receiver for geofence worker
    private val geoFencePendingIntent: PendingIntent by lazy {
        val intent = Intent(appContext, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(appContext, 200, intent, PendingIntent.FLAG_MUTABLE)
    }

    override suspend fun doWork(): Result {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(appContext)
        try {
            userLocation = fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .await()
            if (userLocation == null) {
                // Backup Location Request
                userLocation = fusedLocationClient.lastLocation.await()
                // TODO: instead of using lastLocation as backup, provide a callback to location
                //  provider, and ask to have self rescheduled on location receive
            }
        } catch (e: SecurityException) {
            // TODO: Request Permissions/swap this try-catch to a permissions check + request
            Log.e(TAG, "SecurityException ${e.message}")
            return Result.failure()
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Unexpected error ${e.message} cased by ${e.cause}")
        }
        val eventRepo = EventRepository()
        val events = eventRepo.getEventsByProximity(
            userLocation!!.latitude,
            userLocation!!.longitude,
            DEFAULT_EVENT_FETCH_PROXIMITY)

        val geofences = coroutineScope { events.map { async { buildGeofence(it) } }.awaitAll() }
        if (!registerGeofences(geofences)) {
            return Result.failure()
        }

        return Result.success()
    }

    private fun buildGeofence(event: Event): Geofence {
        return Geofence.Builder()
            .setRequestId(event.eventID)
            .setCircularRegion(
                event.latitude,
                event.longitude,
                DEFAULT_EVENT_RADIUS)
            .setExpirationDuration(FENCE_EXPIRATION) // Delete geofences after 20 minutes
            .setLoiteringDelay(FENCE_LOITER_DELAY) // Notify if within geofence for 2 minutes
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL).build()
    }

    private fun registerGeofences(geofences: List<Geofence>): Boolean {
        val geofencingClient = LocationServices.getGeofencingClient(appContext)
        try {
            geofencingClient.addGeofences(
                GeofencingRequest.Builder()
                    .setInitialTrigger(
                        GeofencingRequest.INITIAL_TRIGGER_ENTER
                                or GeofencingRequest.INITIAL_TRIGGER_DWELL)
                    .addGeofences(geofences)
                    .build(),
                geoFencePendingIntent)
        } catch (e: SecurityException) {
            // TODO: handle permissions requests
            Log.e(
                TAG,
                "Security Error ${e.message} cased by ${e.cause}")
            return false
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Unexpected error ${e.message} cased by ${e.cause}")
            return false
        }
        return true
    }
}