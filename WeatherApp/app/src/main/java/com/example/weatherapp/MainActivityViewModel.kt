package com.example.weatherapp

import android.content.Context
import androidx.lifecycle.*
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.utils.LocationProvider
import kotlinx.coroutines.launch

class MainActivityViewModel(val locationRepository: LocationRepository) : ViewModel() {

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

class MainActivityViewModelFactory(
    private val locationRepository: LocationRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(locationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}