package com.example.weatherapp.ui.nextdayssummary

import androidx.lifecycle.*
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.MockWeatherRepository
import com.example.weatherapp.core.repositories.WeatherRepository

class NextDaysViewModel : ViewModel() {

    private val _nextDaysData = MutableLiveData<List<DayWeatherModel>>()
    val nextDaysData get(): LiveData<List<DayWeatherModel>> = _nextDaysData

    private val _selectedCardIndex = MutableLiveData<Int>()
    val selectedCardIndex get(): LiveData<Int> = _selectedCardIndex

    private val _todayBigCardData = MediatorLiveData<Pair<List<DayWeatherModel>?, Int?>>().apply {
        addSource(nextDaysData) { value = it to value?.second }
        addSource(selectedCardIndex) { value = value?.first to it }
    }

    val todayBigCardData get(): LiveData<Pair<List<DayWeatherModel>, Int>> = Transformations.map(
        _todayBigCardData
    ) {
        val nextDays = it.first
        val selectedIndex = it.second

        if (nextDays == null || selectedIndex == null)
            null
        else
            nextDays to selectedIndex
    }

    private lateinit var weatherRepository: WeatherRepository

    fun updateSelectedCardIndex(index: Int) {
        _selectedCardIndex.value = index
    }

    init {
        weatherRepository = MockWeatherRepository()
        _nextDaysData.value = weatherRepository.getNext7DaysWeatherData()
        _selectedCardIndex.value = 12
    }

}