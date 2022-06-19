package com.example.weatherapp.core.datasources.local.databases

import android.content.Context
import androidx.room.*
import java.time.LocalDate

@Database(entities = [DayWeather::class, HourWeather::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDatabase: RoomDatabase() {
    abstract val dayWeatherDao: DayWeatherDao
    abstract val hourWeatherDao: HourWeatherDao

    companion object {
        private lateinit var INSTANCE: WeatherDatabase

        fun getDatabase(context: Context): WeatherDatabase {
            synchronized(WeatherDatabase::class.java) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        WeatherDatabase::class.java,
                        "weather").build()
                }
            }
            return INSTANCE
        }
    }
}