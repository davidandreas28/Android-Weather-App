package com.example.weatherapp

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.models.Location
import com.example.weatherapp.core.repositories.LocationRepository

class MainActivityViewModel : ViewModel() {

    private val _location = MutableLiveData<Location>()
    val location get() = _location

    private val locationRepository: LocationRepository

    init {
        locationRepository = LocationRepository()
        _location.value = Location("Bucharest", "Romania", 23.5, 22.0)
    }

    fun setLocation(context: Context, latitude: Double, longitude: Double) {
        val newLocation = locationRepository.provideLocation(context, latitude, longitude)
        newLocation?.let {
            _location.value = it
            LocationSharedPrefs.saveLocation(it)
        }
    }
}

fun Location.asString(): String {
    return "$city, $country"
}