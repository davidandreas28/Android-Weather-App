package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import java.time.LocalDate

class MockWeatherRepository : WeatherRepository {

    private var localDateTimeProvider: DateTime = LocalDateTimeImpl()

    override fun getTodayWeatherData(): DayWeatherModel {
        return buildDayWeatherObject()
    }

    override fun getWeatherDataOnDate(): DayWeatherModel {
        TODO("Not yet implemented")
    }

    override fun getNext7DaysWeatherData(): List<DayWeatherModel> {
        val dayWeatherObject = buildDayWeatherObject()
        val nextDaysForecast = List(7) {
            dayWeatherObject
        }

        return nextDaysForecast
    }

    // Hard-coded data for now
    fun buildDayWeatherObject(): DayWeatherModel {
        val hourWeatherList = mutableListOf<HourWeatherModel>()

        for (i in 0..23) {
            val time = if (i / 10 == 0) "0$i:00" else "$i:00"
            val hourWeatherObj = HourWeatherModel(
                time,
                WeatherType.valueOf("SUNNY"),
                23,
                137,
                56.4,
                89.6,
                123.0,
                123.0,
                77.3,
                25,
                123,
                30,
                123,
                12,
                100
            )
            hourWeatherList.add(hourWeatherObj)
        }
        val nowDate = localDateTimeProvider.getDateTime().toLocalDate()
        return DayWeatherModel(
            nowDate,
            hourWeatherList
        )
    }
}