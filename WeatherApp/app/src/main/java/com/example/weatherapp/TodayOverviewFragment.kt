package com.example.weatherapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentTodayOverviewBinding
import com.example.weatherapp.viewmodels.DetailedWeatherViewModel

class TodayOverviewFragment : Fragment() {

    private lateinit var binding: FragmentTodayOverviewBinding
    private val sharedViewModel: DetailedWeatherViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentBinding = FragmentTodayOverviewBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        sharedViewModel.buildHourlyTempList()
        sharedViewModel.buildDetailedWeather()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDetailedCard()
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = HourlyListAdapter(sharedViewModel.hourlyTempList)
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun setupDetailedCard() {
        val liveDataValue = sharedViewModel.detailedWeather.value!!

        binding.detailedCardTime.text = getString(
            R.string.detailed_card_time,
            liveDataValue.timeframe
        )
        binding.detailedIcon.setImageResource(
            liveDataValue.iconSrc
        )

        binding.detailedCardTemp.text = getString(
            R.string.current_temp,
            liveDataValue.temperature.toString(),
            "°C"
        )

        binding.detailedCardWind.text = getString(
            R.string.current_wind_speed,
            liveDataValue.wind.toString(),
            "km/h"
        )

        binding.detailedCardFeelsLike.text = getString(
            R.string.feels_like_temp,
            liveDataValue.feelsLike.toString(),
            "°C"
        )

        binding.detailedCardHumidity.text = getString(
            R.string.humidity_value,
            liveDataValue.humidity.toString()
        )

        binding.pressureValue.text = getString(
            R.string.detailed_card_pressure,
            liveDataValue.pressure.toString(),
            "mbar"
        )
    }
}