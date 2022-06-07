package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.local.SettingsSharedPrefs
import com.example.weatherapp.core.datasources.remote.ServiceConfig
import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.repositories.MockWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository
import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailedWeatherViewModel : ViewModel() {

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

    private var weatherRepository: WeatherRepository

    init {
        weatherRepository = MockWeatherRepository()
        _cardIndexSelected.value = dateTimeProvider.getDateTime().hour
    }

    fun updateSelectedCardIndex(index: Int) {
        _cardIndexSelected.value = index
    }

    fun getWeatherData() {
        val locationData: Pair<Double, Double> = LocationSharedPrefs.getLocation()
        val locationFormatted = "${locationData.first},${locationData.second}"

        viewModelScope.launch(Dispatchers.IO) {
            requestWeatherData(locationFormatted)
        }
    }

    private suspend fun requestWeatherData(location: String) {
        val serviceConfig = ServiceConfig()
        val weatherApiResponse = WeatherApi.retrofitService.getWeatherData(
            serviceConfig.API_KEY,
            location
        )
        val todayWeatherData = weatherApiResponse.forecast.forecastDay[0]

        _todayWeatherData.postValue(weatherRepository.setTodayWeatherData(todayWeatherData))
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