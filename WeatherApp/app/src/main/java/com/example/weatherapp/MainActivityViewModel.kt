package com.example.weatherapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.models.Location
import com.example.weatherapp.core.utils.LocationProvider

class MainActivityViewModel : ViewModel() {

    private val _location = MutableLiveData<Location>()
    val location get() = _location

    fun setLocation(context: Context, latitude: Double, longitude: Double) {
        val newLocation = LocationProvider.provideLocation(context, latitude, longitude)
        newLocation?.let {
            _location.value = it
            Log.i("LOCATION_SAVE", it.asString())
            LocationSharedPrefs.saveLocation(it)
        }
    }
}

fun Location.asString(): String {
    return "$city, $country"
}