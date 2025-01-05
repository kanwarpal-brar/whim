package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.events.EventRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class ReserveEventActivity : AppCompatActivity() {

    private lateinit var eventRepository: EventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        eventRepository = EventRepository()

        val buttonReserveEvent = Button(this).apply {
            text = "Reserve Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }
        val buttonLeaveEvent = Button(this).apply {
            text = "Leave Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        val buttonCheckInEvent = Button(this).apply {
            text = "Check In Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        val buttonCheckOutEvent = Button(this).apply {
            text = "Check Out Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        val deleteEvent = Button(this).apply {
            text = "Delete Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        val updateEvent = Button(this).apply {
            text = "Update Event"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 16
                marginEnd = 16
                topMargin = 16
            }
        }

        layout.addView(buttonReserveEvent)
        layout.addView(buttonLeaveEvent)
        layout.addView(buttonCheckInEvent)
        layout.addView(buttonCheckOutEvent)
        layout.addView(deleteEvent)
        layout.addView(updateEvent)
        // Set the layout as the content view
        setContentView(layout)
        buttonReserveEvent.setOnClickListener {
            reserveEvent("qrXAsbTXUgPRkYIuZ8NhfhA0CnU2", "0znTIAeCCgzaAV2O4R6q")
        }
        buttonLeaveEvent.setOnClickListener {
            leaveEvent("qrXAsbTXUgPRkYIuZ8NhfhA0CnU2", "0znTIAeCCgzaAV2O4R6q")
        }
        buttonCheckInEvent.setOnClickListener {
            checkInEvent("qrXAsbTXUgPRkYIuZ8NhfhA0CnU2", "0znTIAeCCgzaAV2O4R6q")
        }
        buttonCheckOutEvent.setOnClickListener {
            checkOutEvent("qrXAsbTXUgPRkYIuZ8NhfhA0CnU2", "0znTIAeCCgzaAV2O4R6q")
        }
        updateEvent.setOnClickListener {
            updateEvent("0znTIAeCCgzaAV2O4R6q", "attendeeLimit", 5)
        }
        deleteEvent.setOnClickListener {
            deleteEvent("14qQDFAXlHrm8dq3IJXY")
        }
    }

    private fun reserveEvent(userId: String, eventId: String) {
        lifecycleScope.launch {
            val result = eventRepository.reserveEvent(userId, eventId)
            if (result.isSuccess) {
                Log.d("Reserve Event", "Event reserved successfully")
            } else {
                Log.e("Reserve Event", "Failed to reserve event", result.exceptionOrNull())
            }
        }
    }

    private fun leaveEvent(userId: String, eventId: String) {
        lifecycleScope.launch {
            val result = eventRepository.leaveEvent(userId, eventId)
            if (result.isSuccess) {
                Log.d("Leave Event", "Event left successfully")
            } else {
                Log.e("Leave Event", "Failed to leave event", result.exceptionOrNull())
            }
        }
    }

    private fun checkInEvent(userId: String, eventId: String) {
        lifecycleScope.launch {
            val result = eventRepository.checkIntoEvent(userId, eventId)
            if (result.isSuccess) {
                Log.d("Check Into Event", "Event checked in successfully")
            } else {
                Log.e("Check In of Event", "Event failed to check in", result.exceptionOrNull())
            }
        }
    }
    private fun checkOutEvent(userId: String, eventId: String) {
        lifecycleScope.launch {
            val result = eventRepository.checkOutOfEvent(userId, eventId)
            if (result.isSuccess) {
                Log.d("Check Out Event", "Event checked out successfully")
            } else {
                Log.e("Check Out of Event", "Event failed to check out", result.exceptionOrNull())
            }
        }
    }
    private fun updateEvent(eventId: String, field: String, value: Any) {
        lifecycleScope.launch {
            val result = eventRepository.updateEvent(eventId, field, value)
            if (result.isSuccess) {
                Log.d("Check Out Event", "Event checked out successfully")
            } else {
                Log.e("Check Out of Event", "Event failed to check out", result.exceptionOrNull())
            }
        }
    }

    private fun deleteEvent(eventId: String) {
        lifecycleScope.launch {
            val result = eventRepository.deleteEvent(eventId)
            if (result.isSuccess) {
                Log.d("Check Out Event", "Event checked out successfully")
            } else {
                Log.e("Check Out of Event", "Event failed to check out", result.exceptionOrNull())
            }
        }
    }
}
