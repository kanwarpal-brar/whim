package com.example.myapplication.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.Locale

interface GeocodeResultCallback {
    fun onSuccess(latitude: Double, longitude: Double)
    fun onError(errorMessage: String)
}

// Retrieve coordinates from address in an asynchronous way
// To use this function, create an instance of the callback interface and pass it to the function
// i.e., override onSuccess and onFailure and pass in a longitude/latitude
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getCoordinatesFromAddress(
    context: Context,
    address: String,
    callback: GeocodeResultCallback
) {
    val coder = Geocoder(context, Locale.getDefault())

    //TODO: Look into API requirements
    val geocodeListener = @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    object : Geocoder.GeocodeListener {
        override fun onGeocode(addresses: MutableList<Address>) {
            if (addresses.isEmpty()) {
                callback.onError("Fail to find latitude, longitude")
            } else {
                val location = addresses[0]
                callback.onSuccess(location.latitude, location.longitude)
            }
        }

        override fun onError(errorMessage: String?) {
            callback.onError(errorMessage ?: "Unknown error")
        }
    }

    try {
        coder.getFromLocationName(address, 5, geocodeListener)
    } catch (e: Exception) {
        callback.onError("Fail to find Lat,Lng: ${e.message}")
    }
}



