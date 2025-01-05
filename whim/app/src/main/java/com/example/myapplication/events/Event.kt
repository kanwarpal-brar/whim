package com.example.myapplication.events

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Event(
    val eventID: String,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val eventType: String,
    val attendeeLimit: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val host: String,
    val imageUri: Uri?,
    val attendeeCount: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        eventID = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        startTime = LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        endTime = LocalDateTime.parse(parcel.readString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        eventType = parcel.readString() ?: "",
        attendeeLimit = parcel.readInt(),
        address = parcel.readString() ?: "",
        latitude = parcel.readDouble(),
        longitude = parcel.readDouble(),
        host = parcel.readString() ?: "",
        imageUri = null, // We're not including imageUri in the parcel
        attendeeCount = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventID)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        parcel.writeString(endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        parcel.writeString(eventType)
        parcel.writeInt(attendeeLimit)
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(host)
        parcel.writeInt(attendeeCount)
        // We're not writing imageUri to the parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }
}


data class AnonymousEvent(
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val eventType: String,
    val attendeeLimit: Int,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val host: String,
    val imageUri: Uri?
)