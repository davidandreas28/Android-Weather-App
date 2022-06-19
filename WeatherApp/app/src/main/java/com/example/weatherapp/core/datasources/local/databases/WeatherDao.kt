package com.example.weatherapp.core.datasources.local.databases

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


@Dao
interface DayWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(dayWeather: List<DayWeather>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(dayWeather: DayWeather): Long

    @Delete
    fun delete(dayWeather: DayWeather)

    @Query("SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date = :date")
    fun getDayWeather(date: LocalDate, city: String, country: String): DayWeather

    @Query("SELECT * FROM dayWeather WHERE city = :city AND country = :country ORDER BY date")
    fun getAllDayWeather(city: String, country: String): List<DayWeather>

    @Query("SELECT EXISTS(SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date = :date)")
    fun checkEntryExists(date: LocalDate, city: String, country: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date BETWEEN :startDate AND :endDate)")
    fun checkNextDaysStored(startDate: LocalDate, endDate: LocalDate, city: String, country: String): Boolean
}

@Dao
interface HourWeatherDao {
    @Query("select * from hourWeather where dayId = :id ORDER BY time")
    fun getHourForecast(id: Long): List<HourWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(hourWeather: List<HourWeather>)

    @Delete
    fun delete(hourWeather: HourWeather)
}