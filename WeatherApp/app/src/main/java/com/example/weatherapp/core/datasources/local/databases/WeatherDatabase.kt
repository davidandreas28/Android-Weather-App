package com.example.weatherapp.core.datasources.local.databases

import androidx.room.*

@Database(entities = [DayWeather::class, HourWeather::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val dayWeatherDao: DayWeatherDao
    abstract val hourWeatherDao: HourWeatherDao
}