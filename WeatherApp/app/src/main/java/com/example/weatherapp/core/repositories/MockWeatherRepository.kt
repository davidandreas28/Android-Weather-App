package com.example.weatherapp.core.repositories

import android.util.Log
import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MockWeatherRepository : WeatherRepository {

    private var localDateTimeProvider: DateTime = LocalDateTimeImpl()

    override fun setTodayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel {
        val weatherDataHourList: MutableList<HourWeatherModel> = mutableListOf()
        val date = LocalDate.parse(weatherData.date)
        val dayDetails = weatherData.day

        for (dayHourWeather in weatherData.hour) {
            val formattedWeatherType = dayHourWeather.condition.text.replace(" ", "_").uppercase()
            val dayHourWeatherObj = HourWeatherModel(
                getHourString(dayHourWeather.time),
                WeatherType.valueOf(formattedWeatherType),
                dayHourWeather.tempC,
                dayHourWeather.tempF,
                dayHourWeather.windMph,
                dayHourWeather.windKph,
                dayHourWeather.pressureMb,
                dayHourWeather.pressureIn,
                dayHourWeather.humidity,
                dayHourWeather.feelsLikeC,
                dayHourWeather.feelsLikeF,
                dayDetails.maxTempC,
                dayDetails.maxTempF,
                dayDetails.minTempC,
                dayDetails.minTempF
            )
            weatherDataHourList.add(dayHourWeatherObj)
        }

        return DayWeatherModel(
            date,
            weatherDataHourList
        )
    }

    override fun setNextDaysWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel> {
        val nextDaysWeatherList: MutableList<DayWeatherModel> = mutableListOf()
        val weatherData = weatherData.forecast.forecastDay
        Log.d("DATA_SIZE", weatherData.size.toString())

        for (weatherApiObj in weatherData.subList(1, weatherData.size)) {
            val dayWeatherDetails = setTodayWeatherData(weatherApiObj)
            nextDaysWeatherList.add(dayWeatherDetails)
        }
        return nextDaysWeatherList
    }

    fun getHourString(time: String): String {
        return time.takeLast(5)
    }
}