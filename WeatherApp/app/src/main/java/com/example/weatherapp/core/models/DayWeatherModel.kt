package com.example.weatherapp.core.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

data class DayWeatherModel(
    val date: LocalDate,
    val hourlyWeatherList: List<HourWeatherModel>
) {
    fun getFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val dayOfMonth = date.dayOfWeek.getDisplayName(
            TextStyle.FULL, Locale.getDefault()
        )
        val dateFormatted = date.format(formatter)
        return "$dayOfMonth, $dateFormatted"
    }
}
