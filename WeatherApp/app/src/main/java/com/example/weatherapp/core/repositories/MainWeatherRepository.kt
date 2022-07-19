package com.example.weatherapp.core.repositories

import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.HourWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.remote.*
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.utils.DispatcherProvider
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneOffset

class MainWeatherRepository(
    private val database: WeatherDatabase,
    private val dispatcherProvider: DispatcherProvider,
    private val weatherApi: WeatherApi
) : WeatherRepositoryInterface {

    override fun setOneDayWeatherData(weatherData: WeatherForecastDayDetails): DayWeatherModel {
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

    override fun setMultipleDayWeatherData(weatherData: WeatherApiModel): List<DayWeatherModel> {
        val nextDaysWeatherList: MutableList<DayWeatherModel> = mutableListOf()
        val weatherDataObj = weatherData.forecast.forecastDay

        for (weatherApiObj in weatherDataObj.subList(1, weatherDataObj.size)) {
            val dayWeatherDetails = setOneDayWeatherData(weatherApiObj)
            nextDaysWeatherList.add(dayWeatherDetails)
        }
        return nextDaysWeatherList
    }

    override suspend fun requestWeatherData(
        location: LocationData,
        days: Int
    ): NetworkResultWrapper<WeatherApiModel> {

        return withContext(dispatcherProvider.IO()) {
            val serviceConfig = ServiceConfig()
            try {
                val result = weatherApi.getService().getWeatherData(
                    serviceConfig.API_KEY,
                    location.lat.toString() + "," + location.lon.toString(),
                    days
                )
                NetworkResultWrapper.Success(result)
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> NetworkResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        NetworkResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        NetworkResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }

    override suspend fun storeTodayWeather(
        dayWeatherModel: DayWeatherModel,
        location: LocationData
    ) {
        withContext(dispatcherProvider.IO()) {
            val dayWeatherObj = DayWeather(
                date = dayWeatherModel.date,
                city = location.city,
                country = location.country,
                createdAt = LocalDateTimeImpl.getDateTime().toEpochSecond(ZoneOffset.UTC)
            )
            val id = database.dayWeatherDao.insertOne(dayWeatherObj)

            val hourlyWeatherDataList = dayWeatherModel.hourlyWeatherList.map {
                HourWeather(
                    it.time,
                    id,
                    it.weatherType,
                    it.tempC,
                    it.tempF,
                    it.windMph,
                    it.windKph,
                    it.pressureMb,
                    it.pressureIn,
                    it.humidity,
                    it.feelsLikeC,
                    it.feelsLikeF,
                    it.maxTempC,
                    it.maxTempF,
                    it.minTempC,
                    it.minTempF
                )
            }
            database.hourWeatherDao.insertAll(hourlyWeatherDataList)
        }
    }

    override suspend fun storeNextDaysWeather(
        dayWeatherList: List<DayWeatherModel>,
        location: LocationData
    ) {
        for (dayWeatherObj in dayWeatherList) {
            storeTodayWeather(dayWeatherObj, location)
        }
    }


    private fun getHourString(time: String): String {
        return time.takeLast(5)
    }
}