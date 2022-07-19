package com.example.weatherapp.ui.todayoverview

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.repositories.LocationData
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.utils.TestDispatcherProviderImpl
import com.example.weatherapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@RunWith(JUnit4::class)
class DetailedWeatherViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var localDateTimeImpl: DateTime

    @Mock
    private lateinit var database: WeatherDatabase

    @Mock
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    @Mock
    private lateinit var weatherApi: WeatherApi

    @Mock
    private lateinit var locationRepository: LocationRepository

    private lateinit var detailedWeatherViewModel: DetailedWeatherViewModel

    private val localDateTime = LocalDateTime.of(2021, 1, 1, 1, 1)

    @Before
    fun setUp() {
        val dispatcherProvider = TestDispatcherProviderImpl()

        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(dispatcherProvider.Main())

        whenever(localDateTimeImpl.getDateTime()).thenReturn(
            localDateTime
        )
        whenever(userPreferencesRepository.userPreferencesFlow).thenReturn(
            flowOf()
        )
        whenever(locationRepository.locationData).thenReturn(
            flowOf()
        )

        detailedWeatherViewModel = DetailedWeatherViewModel(
            database,
            localDateTimeImpl,
            userPreferencesRepository,
            locationRepository,
            dispatcherProvider,
            weatherApi
        )
    }

    @Test
    fun testUpdateSelectedCardIndex() = runBlocking {
        detailedWeatherViewModel.updateSelectedCardIndex(2)
        Assert.assertEquals(2, detailedWeatherViewModel.cardIndexSelected.getOrAwaitValue())
    }

//    @Test
//    fun testDeleteExpiredData() = runBlocking {
//        detailedWeatherViewModel.deleteExpiredData()
//        verify(database, times(1)).dayWeatherDao.deletePastData(localDate.toLocalDate())
//    }

    @Test
    fun testProvideWeatherData() = runBlocking {
        val location = LocationData(10.0, 10.0, "Example", "Example")
        detailedWeatherViewModel.provideWeatherData(location)
        Assert.assertEquals(location, detailedWeatherViewModel.lastKnownLocation)
    }

//    @Test
//    fun setDayWeatherNewData() = runBlocking {
//        detailedWeatherViewModel.setDayWeatherNewData(DayWeather(
//            1,
//            localDateTime.toLocalDate(),
//            "Example",
//            "Example",
//            1111111
//        ))
//    }


}