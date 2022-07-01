package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.toModel
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.*
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DetailedWeatherViewModel(
    val database: WeatherDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val BASE_STALE_PERIOD = 5 // in hours

    private val _todayWeatherData = MutableLiveData<DayWeatherModel>()
    val todayWeatherData get(): LiveData<DayWeatherModel?> = _todayWeatherData

    private val _cardIndexSelected = MutableLiveData<Int?>()
    val cardIndexSelected get(): LiveData<Int?> = _cardIndexSelected

    val userPreferences = userPreferencesRepository.userPreferencesFlow.asLiveData()

    val locationData = locationRepository.locationData.asLiveData()

    private val _todayBigCardData =
        MediatorLiveData<Triple<DayWeatherModel?, Int?, UserPreferences?>>().apply {
            addSource(todayWeatherData) { value = Triple(it, value?.second, value?.third) }
            addSource(cardIndexSelected) { value = Triple(value?.first, it, value?.third) }
            addSource(userPreferences) { value = Triple(value?.first, value?.second, it) }
        }

    val todayBigCardData
        get(): LiveData<Triple<DayWeatherModel?, Int?, UserPreferences?>> = _todayBigCardData

    private var weatherRepositoryInterface: WeatherRepositoryInterface = MainWeatherRepository(database)

    init {
        _cardIndexSelected.value = LocalDateTimeImpl.getDateTime().hour
    }

    fun updateSelectedCardIndex(index: Int) {
        _cardIndexSelected.value = index
    }

    fun deleteExpiredData() {
        viewModelScope.launch {
            database.dayWeatherDao.deletePastData(
                LocalDateTimeImpl.getDateTime().toLocalDate()
            )
        }
    }

    fun provideWeatherData(location: LocationData) {
        updateWeatherData(LocalDateTimeImpl.getDateTime().toLocalDate(), location)
    }

    private suspend fun setDayWeatherNewData(dayWeather: DayWeather) {
        val hourlyList = database.hourWeatherDao.getHourForecast(dayWeather.id).toModel()
        _todayWeatherData.postValue(
            DayWeatherModel(
                dayWeather.date,
                hourlyList
            )
        )
    }

    private fun updateWeatherData(date: LocalDate, location: LocationData) {

        viewModelScope.launch {
            val entryExists = checkWeatherIsStored(date, location.city, location.country)
            val entryIsStale = checkWeatherDataIsStale(date, location.city, location.country)

            if (!entryExists || (entryExists && entryIsStale)) {
                val networkResponse = weatherRepositoryInterface.requestWeatherData(location, 1)
                val newData =
                    weatherRepositoryInterface.setOneDayWeatherData(networkResponse.forecast.forecastDay[0])
                weatherRepositoryInterface.storeTodayWeather(newData, location)
            }

            val dayWeather =
                database.dayWeatherDao.getDayWeather(date, location.city, location.country)
                    ?: return@launch
            setDayWeatherNewData(dayWeather)
        }
    }

    private suspend fun checkWeatherIsStored(
        date: LocalDate,
        city: String,
        country: String
    ): Boolean {
        return database.dayWeatherDao.checkEntryExists(date, city, country)
    }

    private suspend fun checkWeatherDataIsStale(
        date: LocalDate,
        city: String,
        country: String
    ): Boolean {
        val weatherData = database.dayWeatherDao.getDayWeather(date, city, country) ?: return true
        val dateTimeOfCreation =
            LocalDateTime.ofEpochSecond(weatherData.createdAt, 0, ZoneOffset.UTC)
        if (Duration.between(dateTimeOfCreation, LocalDateTimeImpl.getDateTime())
                .toHours() > BASE_STALE_PERIOD
        ) {
            return true
        }

        return false
    }
}

class DetailedWeatherViewModelFactory(
    private val database: WeatherDatabase,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val locationRepository: LocationRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedWeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailedWeatherViewModel(
                database,
                userPreferencesRepository,
                locationRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}