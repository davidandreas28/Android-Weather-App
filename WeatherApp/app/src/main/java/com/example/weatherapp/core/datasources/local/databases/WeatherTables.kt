package com.example.weatherapp.core.datasources.local.databases

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import java.time.LocalDate

@Entity(tableName = "dayWeather")
data class DayWeather(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val city: String,
    val country: String
)

@Entity(
    primaryKeys = ["time", "dayId"],
    foreignKeys = [
        ForeignKey(
            entity = DayWeather::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = CASCADE
        )], tableName = "hourWeather"
)
data class HourWeather(
    val time: String,
    val dayId: Long,
    val weatherType: WeatherType,
    val tempC: Float,
    val tempF: Float,
    val windMph: Float,
    val windKph: Float,
    val pressureMb: Float,
    val pressureIn: Float,
    val humidity: Int,
    val feelsLikeC: Float,
    val feelsLikeF: Float,
    val maxTempC: Float,
    val maxTempF: Float,
    val minTempC: Float,
    val minTempF: Float
)

fun List<HourWeather>.toModel(): List<HourWeatherModel> {
    return map { hourWeather ->
        HourWeatherModel(
            hourWeather.time,
            hourWeather.weatherType,
            hourWeather.tempC,
            hourWeather.tempF,
            hourWeather.windMph,
            hourWeather.windKph,
            hourWeather.pressureMb,
            hourWeather.pressureIn,
            hourWeather.humidity,
            hourWeather.feelsLikeC,
            hourWeather.feelsLikeF,
            hourWeather.maxTempC,
            hourWeather.maxTempF,
            hourWeather.minTempC,
            hourWeather.minTempF
        )
    }
}

