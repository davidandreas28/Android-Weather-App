package com.example.weatherapp.ui.todayoverview

import android.Manifest
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.models.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import android.location.Geocoder

import androidx.core.app.ActivityCompat
import android.location.LocationManager
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService

class MainActivityViewModel : ViewModel() {

    private val _location = MutableLiveData<Location>()
    val location get() = _location

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        _location.value = Location("Bucharest", "Romania", 23.5, 22.0)
    }

    fun setLocation(context: Context, latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context)
        val locationList = geocoder.getFromLocation(latitude, longitude, 1)
        if (!locationList.isEmpty()) {
            val newLocation = locationList[0]
            _location.value = Location(
                newLocation.locality,
                newLocation.countryName,
                latitude,
                longitude
            )
        }
    }

}