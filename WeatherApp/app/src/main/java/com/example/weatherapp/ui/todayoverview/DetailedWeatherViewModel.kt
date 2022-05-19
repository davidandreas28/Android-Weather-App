package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.MainRepository
import java.time.LocalTime

class DetailedWeatherViewModel : ViewModel() {

    private val _todayWeatherData = MutableLiveData<DayWeatherModel>()
    val todayWeatherData get() = _todayWeatherData

    private val _cardIndexSelected = MutableLiveData<Int>()
    val cardIndexSelected get() = _cardIndexSelected

    fun initWeatherData() {
        _todayWeatherData.value = MainRepository.getTodayWeatherData()
        _cardIndexSelected.value = LocalTime.now().hour
    }
}