package com.example.weatherapp.ui.todayoverview

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.toModel
import com.example.weatherapp.core.datasources.remote.NetworkResultWrapper
import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.*
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.utils.DispatcherProvider
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class DetailedWeatherViewModel @Inject constructor(
    private val database: WeatherDatabase,
    private val localDateTimeImpl: DateTime,
    userPreferencesRepository: UserPreferencesRepository,
    locationRepository: LocationRepository,
    private val dispatcherProvider: DispatcherProvider,
    weatherApi: WeatherApi
) : ViewModel() {

    private val BASE_STALE_PERIOD = 5 // hours

    private val _todayWeatherData = MutableLiveData<DayWeatherModel>()
    val todayWeatherData get(): LiveData<DayWeatherModel?> = _todayWeatherData

    private val _cardIndexSelected = MutableLiveData<Int?>()
    val cardIndexSelected get(): LiveData<Int?> = _cardIndexSelected

    private val _networkStatus = MutableLiveData(1)
    val networkStatus: LiveData<Int> = _networkStatus
    var lastKnownLocation: LocationData? = null

    private val userPreferences = userPreferencesRepository.userPreferencesFlow.asLiveData()

    val locationData = locationRepository.locationData.asLiveData()

    private val _todayBigCardData =
        MediatorLiveData<Triple<DayWeatherModel?, Int?, UserPreferences?>>().apply {
            addSource(todayWeatherData) { value = Triple(it, value?.second, value?.third) }
            addSource(cardIndexSelected) { value = Triple(value?.first, it, value?.third) }
            addSource(userPreferences) { value = Triple(value?.first, value?.second, it) }
        }

    val todayBigCardData
        get(): LiveData<Triple<DayWeatherModel?, Int?, UserPreferences?>> = _todayBigCardData

    private var weatherRepository: WeatherRepositoryInterface =
        MainWeatherRepository(database, dispatcherProvider, weatherApi)

    init {
        _cardIndexSelected.postValue(localDateTimeImpl.getDateTime().hour)
    }

    fun updateSelectedCardIndex(index: Int) {
        _cardIndexSelected.value = index
    }

    fun deleteExpiredData() {
        viewModelScope.launch(dispatcherProvider.Main()) {
            database.dayWeatherDao.deletePastData(
                localDateTimeImpl.getDateTime().toLocalDate()
            )
        }
    }

    fun provideWeatherData(location: LocationData) {
        lastKnownLocation = location
        updateWeatherData(localDateTimeImpl.getDateTime().toLocalDate(), location)
    }

    suspend fun setDayWeatherNewData(dayWeather: DayWeather) {
        val hourlyList = database.hourWeatherDao.getHourForecast(dayWeather.id).toModel()
        _todayWeatherData.postValue(
            DayWeatherModel(
                dayWeather.date,
                hourlyList
            )
        )
    }

    private fun updateWeatherData(date: LocalDate, location: LocationData) {

        viewModelScope.launch(dispatcherProvider.Main()) {
            val entryExists = checkWeatherIsStored(date, location.city, location.country)
            val entryIsStale = checkWeatherDataIsStale(date, location.city, location.country)

            if (!entryExists || (entryExists && entryIsStale)) {
                _networkStatus.value = 1
                val networkResponse = weatherRepository.requestWeatherData(location, 1)
                when (networkResponse) {
                    is NetworkResultWrapper.Success -> {
                        val newData =
                            weatherRepository.setOneDayWeatherData(networkResponse.value.forecast.forecastDay[0])
                        weatherRepository.storeTodayWeather(newData, location)
                        _networkStatus.value = 0
                    }
                    is NetworkResultWrapper.NetworkError -> {
                        Log.e("Network error", "Network error: IO Exception")
                        _networkStatus.value = -1
                    }
                    is NetworkResultWrapper.GenericError -> {
                        Log.e(
                            "Network error",
                            "Message: ${networkResponse.error?.error?.message ?: "generic"}"
                        )
                    }
                }
            }

            val dayWeather =
                database.dayWeatherDao.getDayWeather(date, location.city, location.country)
                    ?: return@launch
            setDayWeatherNewData(dayWeather)
            _networkStatus.postValue(0)
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
        if (Duration.between(dateTimeOfCreation, localDateTimeImpl.getDateTime())
                .toHours() > BASE_STALE_PERIOD
        ) {
            return true
        }

        return false
    }
}