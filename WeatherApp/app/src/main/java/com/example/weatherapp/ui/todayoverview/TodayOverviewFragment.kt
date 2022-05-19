package com.example.weatherapp.ui.todayoverview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.ui.nextdayssummary.NextDaysFragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentTodayOverviewBinding

class TodayOverviewFragment : Fragment() {

    private lateinit var binding: FragmentTodayOverviewBinding
    private val weatherData: DetailedWeatherViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentTodayOverviewBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        weatherData.initWeatherData()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDetailedCard()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter =
            HourlyListAdapter(weatherData.todayWeatherData.value!!.hourlyWeatherList)
        binding.recyclerView.setHasFixedSize(true)

        binding.outlinedButton.setOnClickListener {
            parentFragmentManager.commit {
                replace<NextDaysFragment>(R.id.fragment_container_view)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    private fun setupDetailedCard() {
        val hourWeatherObj =
            weatherData.todayWeatherData.value!!.hourlyWeatherList[weatherData.cardIndexSelected.value!!]

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