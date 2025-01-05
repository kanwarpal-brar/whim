package com.example.myapplication.receivers

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.myapplication.services.EventsNotificationService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null || context == null) {
            Log.e(TAG, "Context or Intent is null")
            return
        }

        lateinit var geofencingEvent: GeofencingEvent
        try {
            geofencingEvent = GeofencingEvent.fromIntent(intent!!) as GeofencingEvent
        } catch (e: Exception) {
            // Error; early terminate
            Log.e(TAG, "Failed to get geofencing event", e)
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.i(TAG, "Geofence entered")
            val triggeredGeofences = geofencingEvent.triggeringGeofences
            if (triggeredGeofences != null) {
                val notificationService = EventsNotificationService(context!!)
                notificationService.showNotification(triggeredGeofences)
            }
        }
    }

}