package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.models.DayWeatherModel

interface WeatherRepository {
    fun getTodayWeatherData(): DayWeatherModel
    fun getWeatherDataOnDate(): DayWeatherModel
    fun getNext7DaysWeatherData(): List<DayWeatherModel>
}