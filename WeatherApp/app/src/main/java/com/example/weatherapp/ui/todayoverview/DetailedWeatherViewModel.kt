package com.example.weatherapp.ui.todayoverview

import androidx.lifecycle.*
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.repositories.MockWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository
import java.time.LocalTime

class DetailedWeatherViewModel : ViewModel() {

    private val _todayWeatherData = MutableLiveData<DayWeatherModel>()
    val todayWeatherData get(): LiveData<DayWeatherModel?> = _todayWeatherData

    private val _cardIndexSelected = MutableLiveData<Int?>()
    val cardIndexSelected get(): LiveData<Int?> = _cardIndexSelected

    private val _todayBigCardData = MediatorLiveData<Pair<DayWeatherModel?, Int?>>().apply {
        addSource(todayWeatherData) { value = it to (value?.second) }
        addSource(cardIndexSelected) { value = Pair(value?.first, it) }
    }

    val todayBigCardData
        get(): LiveData<HourWeatherModel> = Transformations.map(_todayBigCardData) {
            val dayWeatherModel = it.first
            val selectedIndex = it.second

            if (dayWeatherModel == null || selectedIndex == null)
                null
            else
                dayWeatherModel.hourlyWeatherList[selectedIndex]
        }

    private var weatherRepository: WeatherRepository

    init {
        weatherRepository = MockWeatherRepository()
        _todayWeatherData.value = weatherRepository.getTodayWeatherData()
        _cardIndexSelected.value = LocalTime.now().hour
    }

    fun updateSelectedCardIndex(index: Int) {
        _cardIndexSelected.value = index
    }
}