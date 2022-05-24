package com.example.weatherapp.ui.nextdayssummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.todayoverview.HourlyListAdapter
import com.example.weatherapp.databinding.FragmentIntraDayWeatherBinding
import com.example.weatherapp.core.models.DayWeatherModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class IntraDayWeatherFragment : Fragment() {

    private lateinit var binding: FragmentIntraDayWeatherBinding
    private val data: NextDaysViewModel by viewModels()
    val formatter = DateTimeFormatter.ofPattern("dd MMM")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentIntraDayWeatherBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = requireArguments().getInt("itemIndex")

        val weatherObject = data.nextDaysData.value!![index]
        setupDetailedCard(weatherObject)
        setupRecycleView(weatherObject)
    }

    private fun setupRecycleView(weatherObject: DayWeatherModel) {
        val updatePressedPosition: (Int) -> Unit = {
            data.selectedCardIndex.value = it
        }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = HourlyListAdapter(
            weatherObject.hourlyWeatherList,
            updatePressedPosition,
            data.selectedCardIndex,
            context!!,
            false
        )
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.scrollToPosition(data.selectedCardIndex.value!!)
    }

    private fun setupDetailedCard(weatherObject: DayWeatherModel) {
        val hourWeatherObj = weatherObject.hourlyWeatherList[data.selectedCardIndex.value!!]
        val dayOfMonth = weatherObject.date.dayOfWeek.getDisplayName(
            TextStyle.FULL, Locale.getDefault()
        )
        val dateFormatted = weatherObject.date.format(formatter)
        val dateComposed = "${dayOfMonth}, ${dateFormatted}"

        binding.todayElement.text = dateComposed
        binding.detailedCardTimeframe.text = getString(
            R.string.detailed_card_time,
            hourWeatherObj.time
        )
        binding.mainWeatherIcon.setImageResource(
            hourWeatherObj.weatherType.imgSrc
        )
        binding.mainTemperatureValue.text = getString(
            R.string.current_temp,
            hourWeatherObj.tempC.toString(),
            "°C"
        )
        binding.windValue.text = getString(
            R.string.current_wind_speed,
            hourWeatherObj.windKph.toString(),
            "km/h"
        )
        binding.feelsLikeValue.text = getString(
            R.string.feels_like_temp,
            hourWeatherObj.feelsLikeC.toString(),
            "°C"
        )
        binding.humidityValue.text = getString(
            R.string.humidity_value,
            hourWeatherObj.humidity.toString()
        )
        binding.pressureValue.text = getString(
            R.string.detailed_card_pressure,
            hourWeatherObj.pressureMb.toString(),
            "mbar"
        )
    }
}