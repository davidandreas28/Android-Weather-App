package com.example.weatherapp.core.utils

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.weatherapp.core.repositories.LocationData
import java.io.IOException
import javax.inject.Inject

interface LocationProvider {
    fun provideLocation(lat: Double, long: Double): LocationData?
}

class LocationProviderImpl @Inject constructor(private val context: Context) : LocationProvider {
    private val RESULTS_NO = 1
    private val TAG = javaClass.simpleName

    override fun provideLocation(lat: Double, long: Double): LocationData? {
        val geocoder = Geocoder(context)
        try {
            val locationList = geocoder.getFromLocation(lat, long, RESULTS_NO)
            if (locationList.isEmpty()) {
                return null
            }

            val newLocation = locationList[0]
            return LocationData(
                lat,
                long,
                newLocation.locality ?: "Unknown",
                newLocation.countryName ?: "Unknown"
            )
        } catch (e: IOException) {
            Log.e(TAG, "Geocoder service error " + e.message)
            return null
        }
    }
}