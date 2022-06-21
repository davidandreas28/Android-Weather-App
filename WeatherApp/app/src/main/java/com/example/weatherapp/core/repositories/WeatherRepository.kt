package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.Location

interface WeatherRepository {
    fun setOneDayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel
    fun setMultipleDayWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel>
    suspend fun requestWeatherData(location: Location, days: Int): WeatherApiModel
    suspend fun storeTodayWeather(dayWeatherModel: DayWeatherModel, location: Location)
    suspend fun storeNextDaysWeather(dayWeatherList: List<DayWeatherModel>, location: Location)
}