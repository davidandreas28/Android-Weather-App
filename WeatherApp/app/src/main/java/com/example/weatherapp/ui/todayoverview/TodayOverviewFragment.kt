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
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? MainActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        setupDetailedCard()
        setupRecycleView()

        binding.outlinedButton.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                replace<NextDaysFragment>(R.id.fragment_container_view)
                setReorderingAllowed(true)
                addToBackStack(null)
            }
        }
    }

    private fun setupRecycleView() {
        val updatePressedPosition: (Int) -> Unit = {
            weatherData.cardIndexSelected.value = it
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter =
            HourlyListAdapter(
                weatherData.todayWeatherData.value!!.hourlyWeatherList,
                updatePressedPosition,
                weatherData.cardIndexSelected,
                context!!,
                true
            )
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.scrollToPosition(weatherData.cardIndexSelected.value!!)
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