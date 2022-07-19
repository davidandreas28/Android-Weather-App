package com.example.weatherapp.ui.todayoverview

import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.remote.*
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.WeatherType
import com.example.weatherapp.core.repositories.LocationData
import com.example.weatherapp.core.repositories.MainWeatherRepository
import com.example.weatherapp.utils.TestDispatcherProviderImpl
import com.squareup.moshi.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDate

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class MainWeatherRepositoryTest {
    private lateinit var mainWeatherRepository: MainWeatherRepository

    @Mock
    private lateinit var database: WeatherDatabase

    @Mock
    private lateinit var weatherApi: WeatherApi

    @Mock
    private lateinit var weatherModel: WeatherApiModel

    @Mock
    private lateinit var weatherService: WeatherApiService

    @Before
    fun setUp() {
        val dispatcherProvider = TestDispatcherProviderImpl()
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcherProvider.Main())
        mainWeatherRepository = MainWeatherRepository(database, dispatcherProvider, weatherApi)
    }

    @Test
    fun testSetOneDayWeatherData() = runBlocking {
        val hourWeatherModel = provideTestHourWeatherModel()
        val expectedModel = DayWeatherModel(
            LocalDate.of(2020, 1, 1),
            List(24) { hourWeatherModel }
        )
        val testHourDetails = List(24) {
            provideTestHourDetails()
        }
        val testDetails = WeatherDayDetails(
            1f,
            1f,
            1f,
            1f
        )
        val testWeatherData = WeatherForecastDayDetails(
            "2020-01-01",
            testDetails,
            testHourDetails
        )

        val resultModel = mainWeatherRepository.setOneDayWeatherData(testWeatherData)
        Assert.assertEquals(expectedModel, resultModel)
    }

    @Test
    fun testSetMultipleDayWeatherData() = runBlocking {
        val testHourDetails = List(24) {
            provideTestHourDetails()
        }
        val testDetails = WeatherDayDetails(
            1f,
            1f,
            1f,
            1f
        )
        val testForecastDay = WeatherForecastDayDetails(
            "2020-01-01",
            testDetails,
            testHourDetails
        )
        val testWeatherForecastDay = WeatherForecastDay(
            List(7) { testForecastDay }
        )
        val testWeatherApiModel = WeatherApiModel(testWeatherForecastDay)


        val hourWeatherModel = provideTestHourWeatherModel()
        val expectedModel = DayWeatherModel(
            LocalDate.of(2020, 1, 1),
            List(24) { hourWeatherModel }
        )

        val expectedModelList = List(6) {
            expectedModel
        }

        val resultModelList = mainWeatherRepository.setMultipleDayWeatherData(testWeatherApiModel)
        Assert.assertEquals(expectedModelList, resultModelList)
    }

    @Test
    fun testRequestWeatherData() = runBlocking {
        val location = LocationData(
            1.0,
            1.0,
            "Example",
            "Example"
        )
        whenever(weatherApi.getService()).thenReturn(
            weatherService
        )

        whenever(weatherService.getWeatherData(any(), any(), any(), any(), any())).thenReturn(
            weatherModel
        )
        val networkResp = mainWeatherRepository.requestWeatherData(location, 1)
        val expectedNetworkResponse = NetworkResultWrapper.Success(weatherModel)

        Assert.assertEquals(expectedNetworkResponse, networkResp)
    }

    private fun provideTestHourWeatherModel(): HourWeatherModel {
        return HourWeatherModel(
            "00:00",
            WeatherType.CLOUDY,
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            1,
            1f,
            1f,
            1f,
            1f,
            1f,
            1f
        )
    }

    private fun provideTestHourDetails(): HourDetails {
        return HourDetails(
            "00:00",
            1f,
            1f,
            WeatherCondition("CLOUDY"),
            1f,
            1f,
            1f,
            1f,
            1f,
            1f,
            1
        )
    }

}