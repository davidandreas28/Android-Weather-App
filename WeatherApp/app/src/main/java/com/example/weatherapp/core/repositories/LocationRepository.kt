package com.example.weatherapp.core.repositories

import android.content.Context
import android.location.Geocoder
import com.example.weatherapp.core.models.Location

class LocationRepository {
    private val RESULTS_NO = 1

    fun provideLocation(context: Context, latitude: Double, longitude: Double): Location? {
        val geocoder = Geocoder(context)
        val locationList = geocoder.getFromLocation(latitude, longitude, RESULTS_NO)
        if (locationList.isEmpty()) {
            return null
        }

        val newLocation = locationList[0]
        return Location(
            newLocation.locality,
            newLocation.countryName,
            latitude,
            longitude
        )
    }
}