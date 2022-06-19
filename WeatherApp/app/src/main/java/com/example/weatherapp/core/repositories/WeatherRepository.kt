package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.Location

interface WeatherRepository {
    fun setTodayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel
    fun setNextDaysWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel>
    suspend fun requestWeatherData(location: Location, days: Int): WeatherApiModel
    suspend fun storeTodayWeather(dayWeatherModel: DayWeatherModel, location: Location)
    suspend fun storeNextDaysWeather(dayWeatherList: List<DayWeatherModel>, location: Location)
}