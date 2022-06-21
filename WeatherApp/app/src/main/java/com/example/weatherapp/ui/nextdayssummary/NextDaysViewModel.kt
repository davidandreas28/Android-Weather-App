package com.example.weatherapp.ui.nextdayssummary

import android.content.Context
import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.toModel
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.Location
import com.example.weatherapp.core.repositories.MainWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.core.utils.LocationProvider
import kotlinx.coroutines.*
import java.time.LocalDate

class NextDaysViewModel(private val database: WeatherDatabase) : ViewModel() {

    private val _nextDaysData = MutableLiveData<List<DayWeatherModel>>()
    val nextDaysData get(): LiveData<List<DayWeatherModel>> = _nextDaysData

    private val _selectedCardIndex = MutableLiveData<Int>()
    val selectedCardIndex get(): LiveData<Int> = _selectedCardIndex

    private val _todayBigCardData = MediatorLiveData<Pair<List<DayWeatherModel>?, Int?>>().apply {
        addSource(nextDaysData) { value = it to value?.second }
        addSource(selectedCardIndex) { value = value?.first to it }
    }

    val todayBigCardData
        get(): LiveData<Pair<List<DayWeatherModel>?, Int?>> = _todayBigCardData

    private val dateTimeProvider: DateTime = LocalDateTimeImpl()
    private var weatherRepository: WeatherRepository = MainWeatherRepository(database)

    fun updateSelectedCardIndex(index: Int) {
        _selectedCardIndex.value = index
    }

    init {
        _selectedCardIndex.value = 12
    }

    fun provideWeatherData(location: Location) {
        updateWeatherData(dateTimeProvider.getDateTime().toLocalDate(), location)
    }

    fun requestLocationDetails(context: Context): Location? {
        val locationData: Pair<Double, Double> = LocationSharedPrefs.getLocation()
        return LocationProvider.provideLocation(context, locationData.first, locationData.second)
    }

    private fun updateWeatherData(now: LocalDate, location: Location) {
        runBlocking(Dispatchers.IO) {
            if (!checkWeatherIsStored(now, location.city, location.country)) {
                val networkResponse = weatherRepository.requestWeatherData(location, 8)
                val newData = weatherRepository.setMultipleDayWeatherData(networkResponse)
                weatherRepository.storeNextDaysWeather(newData, location)
            }

            val dayWeatherList =
                database.dayWeatherDao.getAllDayWeather(location.city, location.country)
            setNextDaysWeatherData(dayWeatherList)
        }
    }

    private fun setNextDaysWeatherData(dayWeatherList: List<DayWeather>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val dayWeatherModelList = mutableListOf<DayWeatherModel>()

                for (dayWeatherObj in dayWeatherList.subList(1, dayWeatherList.size)) {
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
        }
    }

    private suspend fun checkWeatherIsStored(
        date: LocalDate,
        city: String,
        country: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val entryExists = async {
                val startDate = date.plusDays(1)
                val endDate = date.plusDays(7)
                database.dayWeatherDao.checkNextDaysStored(startDate, endDate, city, country)
            }
            entryExists.await()
        }
    }
}