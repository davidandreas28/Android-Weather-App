package com.example.weatherapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.R
import com.example.weatherapp.models.DetailedWeatherModel
import com.example.weatherapp.models.HourlyTemperatureModel

class DetailedWeatherViewModel : ViewModel() {
    private val _currentLocation = MutableLiveData<String>()
    val currentLocation get() = _currentLocation

    private val _currentCity = MutableLiveData<String>("Bucharest")
    val currentCity get() = _currentCity

    private val _currentCountry = MutableLiveData<String>("Romania")
    val currentCountry get() = _currentCountry

    private val _detailedWeather = MutableLiveData<DetailedWeatherModel>()
    val detailedWeather get() = _detailedWeather

    private val _hourlyTempList: MutableList<MutableLiveData<HourlyTemperatureModel>> = mutableListOf()
    val hourlyTempList = _hourlyTempList

    fun buildAppBarTitle(city: String, country: String) {
        _currentLocation.value = "${city}, ${country}"
    }

    fun buildHourlyTempList() {
        for (i in 0..23) {
            val title = if (i / 10 == 0) "0$i:00" else "$i:00"
            val newObj = HourlyTemperatureModel(title)
            val newLiveDataObject = MutableLiveData<HourlyTemperatureModel>()
            newLiveDataObject.value = newObj
            _hourlyTempList.add(newLiveDataObject)
        }
    }

    fun buildDetailedWeather() {
        val detailedWeatherObj = DetailedWeatherModel(
            "11:00 - 12:00",
            R.drawable.ic_drizzle,
            23,
            19,
            23,
        5,
            1015
        )
        _detailedWeather.value = detailedWeatherObj
    }

}