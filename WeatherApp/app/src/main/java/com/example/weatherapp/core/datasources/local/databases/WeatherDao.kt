package com.example.weatherapp.core.datasources.local.databases

import androidx.room.*
import java.time.LocalDate

@Dao
interface DayWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dayWeather: List<DayWeather>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(dayWeather: DayWeather): Long

    @Query("DELETE FROM dayWeather WHERE date < :startDate")
    suspend fun deletePastData(startDate: LocalDate)

    @Query("DELETE FROM dayWeather WHERE date >= :startDate")
    suspend fun deleteIncompleteData(startDate: LocalDate)

    @Query("SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date = :date ORDER BY createdAt DESC LIMIT 1")
    suspend fun getDayWeather(date: LocalDate, city: String, country: String): DayWeather?

    @Query("SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date BETWEEN :startDate AND :endDate ORDER BY date")
    suspend fun getNextDaysData(
        city: String,
        country: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DayWeather>

    @Query("SELECT EXISTS(SELECT * FROM dayWeather WHERE city = :city AND country = :country AND date = :date)")
    suspend fun checkEntryExists(date: LocalDate, city: String, country: String): Boolean

    @Query("SELECT COUNT(DISTINCT date) FROM dayWeather WHERE city = :city AND country = :country AND date BETWEEN :startDate AND :endDate")
    suspend fun retrieveNextDaysNumber(
        startDate: LocalDate,
        endDate: LocalDate,
        city: String,
        country: String
    ): Int
}

@Dao
interface HourWeatherDao {
    @Query("select * from hourWeather where dayId = :id ORDER BY time")
    suspend fun getHourForecast(id: Long): List<HourWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hourWeather: List<HourWeather>)

    @Delete
    suspend fun delete(hourWeather: HourWeather)
}