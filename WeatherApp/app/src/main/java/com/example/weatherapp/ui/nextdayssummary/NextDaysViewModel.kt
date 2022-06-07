package com.example.weatherapp.ui.nextdayssummary

import androidx.lifecycle.*
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.remote.ServiceConfig
import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.MockWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NextDaysViewModel : ViewModel() {

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

    private var weatherRepository: WeatherRepository

    fun updateSelectedCardIndex(index: Int) {
        _selectedCardIndex.value = index
    }

    init {
        weatherRepository = MockWeatherRepository()
        _selectedCardIndex.value = 12
    }

    fun getNextDaysWeather() {
        val serviceConfig = ServiceConfig()
        val locationData: Pair<Double, Double> = LocationSharedPrefs.getLocation()
        val locationFormatted = "${locationData.first},${locationData.second}"

        viewModelScope.launch(Dispatchers.IO) {
            val weatherApiResponse = WeatherApi.retrofitService.getWeatherData(
                serviceConfig.API_KEY,
                locationFormatted,
                8
            )

            _nextDaysData.postValue(weatherRepository.setNextDaysWeatherData(weatherApiResponse))
        }
    }
}