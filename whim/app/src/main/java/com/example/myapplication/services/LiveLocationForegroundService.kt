package com.example.myapplication.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.PermissionChecker
import com.example.myapplication.events.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.time.LocalTime

class LiveLocationForegroundService: Service() {
    companion object {
        const val LOCATION_SERVICE_NOTIF_CHANNEL_ID = "location_service_notif_channel"
        const val LOCATION_REQ_INTERVAL: Long = 60000L*2L // 2 minutes in milliseconds
        // Convenience string for adding extra to passed intents
        const val TARGET_EVENT_EXTRA = "expectedTargetEvent"
        const val EVENT_SAFETY_RADIUS = 850.0 // meters
        const val EVENT_TIME_SAFETY_BUFFER = 1L // 1h buffer
        enum class SafetyLevel { Safe, OutOfBounds, Unresponsive }
    }

    private val notifChannel = NotificationChannel(
        LOCATION_SERVICE_NOTIF_CHANNEL_ID,
        "Location Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation: Location
    private lateinit var targetEvent: Event
    private val targetEventLocation = Location(null)
    private var safetyLevel: SafetyLevel = SafetyLevel.Safe
    private var unsafeTriggers = 0 // Tracks number of times safety level was raised
    private var safeTriggers = 0 // Tracks number of times safety level was lowered
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var expectedEndTime: LocalTime

    private fun createForegroundNotification(): Notification {
        return NotificationCompat.Builder(
            this,
            LOCATION_SERVICE_NOTIF_CHANNEL_ID)
            .setContentTitle("Whim Live Location Security Service")
            .setContentText("Running to monitor your safety")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation) // Add an icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .build()
    }

    private fun dispatchDistanceWarning() {
        val notification = NotificationCompat.Builder(
            this,
            LOCATION_SERVICE_NOTIF_CHANNEL_ID
        )
            .setContentTitle("Warning: Out of Event Bounds")
            .setContentText("Whim has detected that you have left an event without checking out," +
                    " for your safety if you want to leave the event please click the check out" +
                    "button.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.alert_dark_frame)
            .build()
        try {
            NotificationManagerCompat.from(this).notify(
                SafetyLevel.OutOfBounds.ordinal,
                notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send notification", e)
        }
    }

    private fun dispatchTimeWarning() {
        val notification = NotificationCompat.Builder(
            this,
            LOCATION_SERVICE_NOTIF_CHANNEL_ID
        )
            .setContentTitle("Warning: Event Is Over")
            .setContentText("Whim has detected that your event has ended, and you have not left," +
                    " for your safety please leave the area and click the checkout button")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.alert_dark_frame)
            .build()
        try {
            NotificationManagerCompat.from(this).notify(
                SafetyLevel.OutOfBounds.ordinal,
                notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send notification", e)
        }
    }

    private fun dispatchUnresponsiveWarning() {
        val notification = NotificationCompat.Builder(
            this,
            LOCATION_SERVICE_NOTIF_CHANNEL_ID
        )
            .setContentTitle("Received no response. Emergency contact will be notified")
            .setContentText("Whim has detected that you are unresponsive. For your safety, your" +
                    " emergency contact will be notified to check in on you.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.alert_dark_frame)
            .build()
        try {
            NotificationManagerCompat.from(this).notify(
                SafetyLevel.Unresponsive.ordinal,
                notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to send notification", e)
        }
    }

    private fun escalateSafetyLevel(
        outOfBoundsNotif: () -> Unit,
        unresponsiveNotif: () -> Unit
    ) {
        unsafeTriggers++
        // TODO: implement warnings for out of radius
        if (safetyLevel == SafetyLevel.Safe) {
            // Alert out of bounds
            safetyLevel = SafetyLevel.OutOfBounds
            outOfBoundsNotif()
        } else if (safetyLevel == SafetyLevel.OutOfBounds) {
            // User non-responsive'
            safetyLevel = SafetyLevel.Unresponsive
            unresponsiveNotif()
            // TODO: communicate with server to upload lastKnownLocation
        }

    }

    private fun deescalateSafetyLevel() {
        safetyLevel = SafetyLevel.Safe
        safeTriggers++
    }

    private fun assessSafetyByDist(newLocation: Location) {
        if (newLocation.distanceTo(targetEventLocation) > EVENT_SAFETY_RADIUS) {
            escalateSafetyLevel({ dispatchDistanceWarning() }, { dispatchUnresponsiveWarning() })
        }
    }

    private fun assessSafetyByTime(newLocation: Location) {
        if (newLocation.distanceTo(targetEventLocation) <= EVENT_SAFETY_RADIUS &&
            LocalTime.now().isAfter(expectedEndTime)) {
            // User is in event radius and after end time
            escalateSafetyLevel({ dispatchTimeWarning() }, { dispatchUnresponsiveWarning() })
        }
    }

    private fun handleLocation(locationResult: LocationResult) {
        Log.i(TAG, "Location callback with location: ${locationResult.lastLocation}")
        val initialTriggers = unsafeTriggers
        assessSafetyByTime(locationResult.lastLocation!!)
        assessSafetyByDist(locationResult.lastLocation!!)
        if (unsafeTriggers == initialTriggers && safetyLevel != SafetyLevel.Safe) {
            deescalateSafetyLevel()
        }
        lastKnownLocation = locationResult.lastLocation!!
    }

    private fun startLocationMainloop() {
        Log.i(TAG, "Starting Live Location Main loop")
        locationRequest = LocationRequest.Builder(LOCATION_REQ_INTERVAL)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                handleLocation(locationResult)
            }
        }
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to request location updates due to permissions", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request location updates due to error", e)
        }
    }


    private fun startForeground() {
        val locationPerm = PermissionChecker.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (locationPerm != PermissionChecker.PERMISSION_GRANTED) {
            Log.e(TAG, "Missing ACCESS_FINE_LOCATION permission")
            return
        }
        try {
            startForeground(1, createForegroundNotification())
            Log.i(TAG, "Started Live Location in foreground")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground", e)
        }
    }

    private fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i(TAG, "LiveLocationForegroundService destroyed")
        stopForeground()
    }

    /*
    NOTE: triggered on context.startForegroundService(Intent(this, LiveLocationForegroundService::class.java))
    NOTE: expects a targetEvent to be passed in the intent through
        intent.putExtra("targetEvent", targetEvent)
    */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i(TAG, "LiveLocationForegroundService started")
        try {
            targetEvent = intent.getParcelableExtra<Event>(TARGET_EVENT_EXTRA)!!
            targetEventLocation.latitude = targetEvent.latitude
            targetEventLocation.longitude = targetEvent.longitude
            expectedEndTime = targetEvent.startTime
                .toLocalTime()
                .plusHours(EVENT_TIME_SAFETY_BUFFER)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get target event from intent", e)
        }
        startForeground()
        startLocationMainloop()
        return START_STICKY // This will make sure the service is restarted
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "LiveLocationForegroundService created")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notifChannel)
    }

}