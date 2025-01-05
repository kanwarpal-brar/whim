package com.example.myapplication.util

import android.location.Location

fun location(latitude: Double, longitude: Double): Location {
    val location = Location("dummyProvider")
    location.latitude = latitude
    location.longitude = longitude
    return location
}