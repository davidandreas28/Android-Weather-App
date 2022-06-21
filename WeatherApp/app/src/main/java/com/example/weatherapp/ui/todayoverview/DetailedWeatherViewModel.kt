package com.example.weatherapp.ui.todayoverview

import android.content.Context
import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.local.SettingsSharedPrefs
import com.example.weatherapp.core.datasources.local.databases.DayWeather
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.toModel
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.models.Location
import com.example.weatherapp.core.repositories.MainWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.core.utils.LocationProvider
import kotlinx.coroutines.*
import java.time.LocalDate

class DetailedWeatherViewModel(val database: WeatherDatabase) : ViewModel() {

    private val _todayWeatherData = MutableLiveData<DayWeatherModel>()
    val todayWeatherData get(): LiveData<DayWeatherModel?> = _todayWeatherData

    private val _cardIndexSelected = MutableLiveData<Int?>()
    val cardIndexSelected get(): LiveData<Int?> = _cardIndexSelected

    private val _todayBigCardData = MediatorLiveData<Pair<DayWeatherModel?, Int?>>().apply {
        addSource(todayWeatherData) { value = it to (value?.second) }
        addSource(cardIndexSelected) { value = Pair(value?.first, it) }
    }

    private val dateTimeProvider: DateTime = LocalDateTimeImpl()

    val todayBigCardData
        get(): LiveData<Pair<DayWeatherModel?, Int?>> = _todayBigCardData

    private var weatherRepository: WeatherRepository = MainWeatherRepository(database)

    init {
        _cardIndexSelected.value = dateTimeProvider.getDateTime().hour
    }

    fun updateSelectedCardIndex(index: Int) {
        _cardIndexSelected.value = index
    }

    fun requestLocationDetails(context: Context): Location? {
        val locationData: Pair<Double, Double> = LocationSharedPrefs.getLocation()
        return LocationProvider.provideLocation(context, locationData.first, locationData.second)
    }

    fun provideWeatherData(location: Location) {
        updateWeatherData(dateTimeProvider.getDateTime().toLocalDate(), location)
    }

    private fun setDayWeatherNewData(dayWeather: DayWeather) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val hourlyList = database.hourWeatherDao.getHourForecast(dayWeather.id).toModel()
                _todayWeatherData.postValue(
                    DayWeatherModel(
                        dayWeather.date,
                        hourlyList
                    )
                )
            }
        }
    }

    fun updateWeatherData(date: LocalDate, location: Location) {

        runBlocking(Dispatchers.IO) {
            if (!checkWeatherIsStored(date, location.city, location.country)) {
                val networkResponse = weatherRepository.requestWeatherData(location, 1)
                val newData =
                    weatherRepository.setOneDayWeatherData(networkResponse.forecast.forecastDay[0])
                weatherRepository.storeTodayWeather(newData, location)
            }

            val dayWeather =
                database.dayWeatherDao.getDayWeather(date, location.city, location.country)
            setDayWeatherNewData(dayWeather)
        }

    }

    private suspend fun checkWeatherIsStored(
        date: LocalDate,
        city: String,
        country: String
    ): Boolean {

        return withContext(Dispatchers.IO) {
            val entryExists = async {
                database.dayWeatherDao.checkEntryExists(date, city, country)
            }
            entryExists.await()
        }
    }

    companion object {
        fun getTempPref(hourWeatherObj: HourWeatherModel, ending: Boolean): String {
            return if (SettingsSharedPrefs.getTempPrefs())
                "${hourWeatherObj.tempC}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.tempF}${if (ending) "°F" else "°"}"
        }

        fun getFeelsLikeTempPref(hourWeatherObj: HourWeatherModel, ending: Boolean): String {
            return if (SettingsSharedPrefs.getTempPrefs())
                "${hourWeatherObj.feelsLikeC}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.feelsLikeF}${if (ending) "°F" else "°"}"
        }

        fun getPressurePref(hourWeatherObj: HourWeatherModel): String {
            if (SettingsSharedPrefs.getPressurePref())
                return "${hourWeatherObj.pressureMb} mb"
            else
                return "${hourWeatherObj.pressureIn} in"
        }

        fun getMaxTempPref(hourWeatherObj: HourWeatherModel, ending: Boolean): String {
            return if (SettingsSharedPrefs.getTempPrefs())
                "${hourWeatherObj.maxTempC.toInt()}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.maxTempF.toInt()}${if (ending) "°F" else "°"}"
        }

        fun getMinTempPref(hourWeatherObj: HourWeatherModel, ending: Boolean): String {
            return if (SettingsSharedPrefs.getTempPrefs())
                "${hourWeatherObj.minTempC.toInt()}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.maxTempF.toInt()}${if (ending) "°F" else "°"}"
        }
    }
}

class DetailedWeatherViewModelFactory(private val database: WeatherDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailedWeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailedWeatherViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}