package com.example.weatherapp.models

import java.util.Date

data class DayTemperatureModel(
    val date: Date,
    val iconSrc: Int,
    val dayTemp: Int,
    val NightTemp: Int
)