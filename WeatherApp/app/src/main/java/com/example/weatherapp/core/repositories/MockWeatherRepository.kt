package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.remote.WeatherApiModel
import com.example.weatherapp.core.datasources.remote.WeatherForecastDayDetails
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import java.time.LocalDate

class MockWeatherRepository : WeatherRepository {

    override fun setTodayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel {
        val weatherDataHourList: MutableList<HourWeatherModel> = mutableListOf()
        val date = LocalDate.parse(weatherData.date)
        val dayDetails = weatherData.day

        for (dayHourWeather in weatherData.hour) {
            val formattedWeatherType = dayHourWeather.condition.getFormattedCondition()
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
        val weatherDataObj = weatherData.forecast.forecastDay

        for (weatherApiObj in weatherDataObj.subList(1, weatherDataObj.size)) {
            val dayWeatherDetails = setTodayWeatherData(weatherApiObj)
            nextDaysWeatherList.add(dayWeatherDetails)
        }
        return nextDaysWeatherList
    }

    private fun getHourString(time: String): String {
        return time.takeLast(5)
    }
}