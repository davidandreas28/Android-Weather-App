package com.example.weatherapp.core.models

data class HourWeatherModel(
    val time: String,
    val weatherType: WeatherType,
    val tempC: Float,
    val tempF: Float,
    val windMph: Float,
    val windKph: Float,
    val pressureMb: Float,
    val pressureIn: Float,
    val humidity: Int,
    val feelsLikeC: Float,
    val feelsLikeF: Float,
    val maxTempC: Float,
    val maxTempF: Float,
    val minTempC: Float,
    val minTempF: Float
)
