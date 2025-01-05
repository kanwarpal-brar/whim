package com.example.myapplication.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivityImageUpload
import com.example.myapplication.R
import com.google.android.gms.location.Geofence

class EventsNotificationService (
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    fun showNotification(triggeringGeofences: List<Geofence>) {
        // Pending Intent for com.example.myapplication.MainActivity so that user can navigate to APP by clicking
        val activityIntent = Intent(context, MainActivityImageUpload::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val newEventsNumber = triggeringGeofences.count()

        // TO-DO: change notification icon
        val notification = NotificationCompat.Builder(context, EVENTS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Events Nearby")
            .setContentText("There ${if (newEventsNumber == 1) "is" else "are"} $newEventsNumber event${if (newEventsNumber > 1) "s" else ""} nearby. View them on Whim. ")
            .setContentIntent(activityPendingIntent)
            .build()
        notificationManager.notify(1, notification)
    }
    companion object{
        const val EVENTS_CHANNEL_ID = "events_notification_channel"
    }
}

