package com.example.weatherapp.ui.nextdayssummary

import android.util.Log
import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.toModel
import com.example.weatherapp.core.datasources.remote.NetworkResultWrapper
import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.*
import com.example.weatherapp.core.utils.DataState
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.utils.DispatcherProvider
import kotlinx.coroutines.*
import javax.inject.Inject

class NextDaysViewModel @Inject constructor(
    private val database: WeatherDatabase,
    userPreferencesRepository: UserPreferencesRepository,
    locationRepository: LocationRepository,
    dispatcherProvider: DispatcherProvider,
    weatherApi: WeatherApi
) : ViewModel() {

    val DEFAULT_NEXT_DAYS_NUMBER = 2

    private val _nextDaysData = MutableLiveData<List<DayWeatherModel>>()
    val nextDaysData get(): LiveData<List<DayWeatherModel>> = _nextDaysData

    private val _selectedCardIndex = MutableLiveData<Int>()
    val selectedCardIndex get(): LiveData<Int> = _selectedCardIndex

    private val _networkStatus = MutableLiveData(DataState.LOADING)
    val networkStatus: LiveData<DataState> = _networkStatus

    val userPreferences = userPreferencesRepository.userPreferencesFlow.asLiveData()
    val locationData = locationRepository.locationData.asLiveData()

    private val _todayBigCardData =
        MediatorLiveData<Triple<List<DayWeatherModel>?, Int?, UserPreferences?>>().apply {
            addSource(nextDaysData) { value = Triple(it, value?.second, value?.third) }
            addSource(selectedCardIndex) { value = Triple(value?.first, it, value?.third) }
            addSource(userPreferences) { value = Triple(value?.first, value?.second, it) }
        }

    val todayBigCardData
        get(): LiveData<Triple<List<DayWeatherModel>?, Int?, UserPreferences?>> = _todayBigCardData

    private var weatherRepository: WeatherRepositoryInterface =
        MainWeatherRepository(database, dispatcherProvider, weatherApi)

    fun updateSelectedCardIndex(index: Int) {
        _selectedCardIndex.value = index
    }

    init {
        _selectedCardIndex.value = 12
    }

    fun provideWeatherData(location: LocationData) {
        updateWeatherData(location)
    }

    private fun updateWeatherData(location: LocationData) {
        viewModelScope.launch(Dispatchers.IO) {
            if (retrieveHowMuchDataIsStored(
                    location.city,
                    location.country
                ) != DEFAULT_NEXT_DAYS_NUMBER
            ) {
                deleteInvalidData()
                val networkResponse = weatherRepository.requestWeatherData(location, 8)
                when (networkResponse) {
                    is NetworkResultWrapper.Success -> {
                        val newData =
                            weatherRepository.setMultipleDayWeatherData(networkResponse.value)
                        weatherRepository.storeNextDaysWeather(newData, location)
                        _networkStatus.postValue(DataState.SUCCESS)
                    }
                    is NetworkResultWrapper.NetworkError -> {
                        Log.e("Network error", "Network error: IO Exception")
                        _networkStatus.postValue(DataState.ERROR)
                    }
                    is NetworkResultWrapper.GenericError -> {
                        Log.e(
                            "Network error",
                            "Message: ${networkResponse.error?.error?.message ?: "generic"}"
                        )
                        _networkStatus.postValue(DataState.ERROR)
                    }
                }
            }

            val startDate = LocalDateTimeImpl.getDateTime().toLocalDate().plusDays(1)
            val endDate = startDate.plusDays(7)
            val dayWeatherList =
                database.dayWeatherDao.getNextDaysData(
                    location.city,
                    location.country,
                    startDate,
                    endDate
                )
            setNextDaysWeatherData(dayWeatherList)
            _networkStatus.postValue(DataState.SUCCESS)
        }
    }

    private suspend fun deleteInvalidData() {
        withContext(Dispatchers.IO) {
            database.dayWeatherDao.deleteIncompleteData(
                LocalDateTimeImpl.getDateTime().toLocalDate().plusDays(1)
            )
        }
    }

    private suspend fun setNextDaysWeatherData(dayWeatherList: List<DayWeather>) {
        val dayWeatherModelList = mutableListOf<DayWeatherModel>()

        for (dayWeatherObj in dayWeatherList) {
            val hourlyList =
                database.hourWeatherDao.getHourForecast(dayWeatherObj.id).toModel()

            dayWeatherModelList.add(
                DayWeatherModel(
                    dayWeatherObj.date,
                    hourlyList
                )
            )
        }
        _nextDaysData.postValue(dayWeatherModelList)
    }

    private suspend fun retrieveHowMuchDataIsStored(
        city: String,
        country: String
    ): Int {

        val startDate = LocalDateTimeImpl.getDateTime().toLocalDate().plusDays(1)
        val endDate = LocalDateTimeImpl.getDateTime().toLocalDate().plusDays(7)
        return database.dayWeatherDao.retrieveNextDaysNumber(startDate, endDate, city, country)
    }
}