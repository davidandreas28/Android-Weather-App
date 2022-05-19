package com.example.weatherapp.core.models

import java.time.LocalDate

data class DayWeatherModel(
    val date: LocalDate,
    val hourlyWeatherList: List<HourWeatherModel>
    )
