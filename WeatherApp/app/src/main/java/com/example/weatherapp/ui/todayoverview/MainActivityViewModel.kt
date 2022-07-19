package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.utils.LocationProvider
import com.example.weatherapp.utils.DispatcherProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val locationRepository: LocationRepository,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    val location = locationRepository.locationData.asLiveData()

    fun setLocation(latitude: Double, longitude: Double) {
        val newLocation = locationProvider.provideLocation(latitude, longitude)
        newLocation?.let {
            viewModelScope.launch(dispatcherProvider.Main()) {
                locationRepository.updateLocation(newLocation)
            }
        }
    }
}