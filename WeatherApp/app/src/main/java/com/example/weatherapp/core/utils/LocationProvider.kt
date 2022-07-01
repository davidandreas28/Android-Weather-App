package com.example.weatherapp.core.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.weatherapp.core.repositories.LocationData
import java.io.IOException

class LocationProvider {
    companion object {
        private val RESULTS_NO = 1
        private val TAG = javaClass.simpleName

        fun provideLocation(context: Context, latitude: Double, longitude: Double): LocationData? {
            val geocoder = Geocoder(context)
            try {
                val locationList = geocoder.getFromLocation(latitude, longitude, RESULTS_NO)
                if (locationList.isEmpty()) {
                    return null
                }

                val newLocation = locationList[0]
                return LocationData(
                    latitude,
                    longitude,
                    newLocation.locality ?: "Unknown",
                    newLocation.countryName ?: "Unknown"
                )
            } catch (e: IOException) {
                Log.e(TAG, "Geocoder service error " + e.message)
                return null
            }
        }
    }
}