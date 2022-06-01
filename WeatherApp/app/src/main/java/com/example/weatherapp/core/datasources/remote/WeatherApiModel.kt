package com.example.weatherapp.core.datasources.remote

import com.squareup.moshi.Json

data class WeatherApiModel(
    val forecast: WeatherForecastDay
)

data class WeatherForecastDay(
    @Json(name = "forecastday") val forecastDay: List<WeatherForecastDayDetails>
)

data class WeatherForecastDayDetails(
    val date: String,
    val day: WeatherDayDetails,
    val hour: List<HourDetails>
)

data class WeatherDayDetails(
    @Json(name = "maxtemp_c") val maxTempC: Float,
    @Json(name = "maxtemp_f") val maxTempF: Float,
    @Json(name = "mintemp_c") val minTempC: Float,
    @Json(name = "mintemp_f") val minTempF: Float
)

data class HourDetails(
    val time: String,
    @Json(name = "temp_c") val tempC: Float,
    @Json(name = "temp_f") val tempF: Float,
    val condition: WeatherCondition,
    @Json(name = "wind_mph") val windMph: Float,
    @Json(name = "wind_kph") val windKph: Float,
    @Json(name = "pressure_mb") val pressureMb: Float,
    @Json(name = "pressure_in") val pressureIn: Float,
    @Json(name = "feelslike_c") val feelsLikeC: Float,
    @Json(name = "feelslike_f") val feelsLikeF: Float,
    val humidity: Int
    )

data class WeatherCondition(
    val text: String
)


