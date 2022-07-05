package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.remote.NetworkResultWrapper
import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel

interface WeatherRepositoryInterface {
    fun setOneDayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel
    fun setMultipleDayWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel>
    suspend fun requestWeatherData(
        location: LocationData,
        days: Int
    ): NetworkResultWrapper<WeatherApiModel>

    suspend fun storeTodayWeather(dayWeatherModel: DayWeatherModel, location: LocationData)
    suspend fun storeNextDaysWeather(dayWeatherList: List<DayWeatherModel>, location: LocationData)
}