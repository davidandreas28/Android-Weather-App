package com.example.weatherapp.ui.todayoverview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.databinding.FragmentTodayOverviewBinding

class TodayOverviewFragment : Fragment() {

    interface MainActivityLinker {
        fun onNextDaysButtonClicked()
        fun setHomeAsUp()
    }

    private val dummyLinker: MainActivityLinker = object : MainActivityLinker {
        override fun onNextDaysButtonClicked() {}
        override fun setHomeAsUp() {}
    }

    private var mainActivityLinker: MainActivityLinker = dummyLinker
    private lateinit var binding: FragmentTodayOverviewBinding
    private lateinit var adapter: HourlyListAdapter
    private val weatherViewModel: DetailedWeatherViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentTodayOverviewBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivityLinker.setHomeAsUp()
        observe()
        setupRecycleView()

        binding.outlinedButton.setOnClickListener {
            mainActivityLinker.onNextDaysButtonClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivityLinker = context
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            weatherViewModel.updateSelectedCardIndex(it)
        }

        adapter = HourlyListAdapter(
            onItemClicked,
            true
        )

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.scrollToPosition(weatherViewModel.cardIndexSelected.value!!)
    }

    private fun observe() {
        weatherViewModel.cardIndexSelected.observe(viewLifecycleOwner) {
            adapter.selectedIndex = it
        }

        weatherViewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe
            setupDetailedCard(it)
        }

        weatherViewModel.todayWeatherData.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe
            adapter.hourlyTempList = it.hourlyWeatherList
        }
    }

    private fun setupDetailedCard(hourWeatherObj: HourWeatherModel) {

        with(binding) {
            detailedCardTimeframe.text = getString(
                R.string.detailed_card_time,
                hourWeatherObj.time
            )
            mainWeatherIcon.setImageResource(
                hourWeatherObj.weatherType.imgSrc
            )
            mainTemperatureValue.text = getString(
                R.string.current_temp,
                hourWeatherObj.tempC.toString(),
                "°C"
            )
            windValue.text = getString(
                R.string.current_wind_speed,
                hourWeatherObj.windKph.toString(),
                "km/h"
            )
            feelsLikeValue.text = getString(
                R.string.feels_like_temp,
                hourWeatherObj.feelsLikeC.toString(),
                "°C"
            )
            humidityValue.text = getString(
                R.string.humidity_value,
                hourWeatherObj.humidity.toString()
            )
            pressureValue.text = getString(
                R.string.detailed_card_pressure,
                hourWeatherObj.pressureMb.toString(),
                "mbar"
            )
        }
    }
}