package com.example.weatherapp.core.models

data class HourWeatherModel(
    val time: String,
    val weatherType: WeatherType,
    val tempC: Int,
    val tempF: Int,
    val windMph: Double,
    val windKph: Double,
    val pressureMb: Double,
    val pressureIn: Double,
    val humidity: Double,
    val feelsLikeC: Int,
    val feelsLikeF: Int,
    val maxTempC: Int,
    val maxTempF: Int,
    val minTempC: Int,
    val minTempF: Int
)
