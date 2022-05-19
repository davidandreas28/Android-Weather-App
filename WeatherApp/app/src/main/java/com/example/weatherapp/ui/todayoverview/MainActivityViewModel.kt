package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.models.Location

class MainActivityViewModel : ViewModel() {

    private val _location = MutableLiveData<Location>()
    val location get() = _location

    fun initLocation() {
        _location.value = Location("Bucharest", "Romania", 23.5, 22.0)
    }
}