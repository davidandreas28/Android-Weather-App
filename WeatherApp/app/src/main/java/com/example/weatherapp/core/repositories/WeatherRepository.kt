package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel

interface WeatherRepository {
    fun setTodayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel
    fun setNextDaysWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel>
}