package com.example.weatherapp.models

data class DetailedWeatherModel(
    val timeframe: String,
    val iconSrc: Int,
    val temperature: Int,
    val wind: Int,
    val feelsLike: Int,
    val humidity: Int,
    val pressure: Int
)