package com.example.weatherapp.ui.todayoverview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.utils.LocationProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(val locationRepository: LocationRepository) : ViewModel() {

    val location = locationRepository.locationData.asLiveData()

    fun setLocation(context: Context, latitude: Double, longitude: Double) {
        val newLocation = LocationProvider.provideLocation(context, latitude, longitude)
        newLocation?.let {
            viewModelScope.launch {
                locationRepository.updateLocation(newLocation)
            }
        }
    }
}