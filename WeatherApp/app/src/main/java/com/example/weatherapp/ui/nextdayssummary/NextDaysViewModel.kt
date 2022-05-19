package com.example.weatherapp.ui.nextdayssummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.MainRepository

class NextDaysViewModel : ViewModel() {

    private val _nextDaysData = MutableLiveData<List<DayWeatherModel>>()
    val nextDaysData get() = _nextDaysData

    private val _selectedCardIndex = MutableLiveData<Int>()
    val selectedCardIndex get() = _selectedCardIndex

    fun initNextDaysData() {
        _nextDaysData.value = MainRepository.getNext7DaysWeatherData()
        _selectedCardIndex.value = 12
    }
}