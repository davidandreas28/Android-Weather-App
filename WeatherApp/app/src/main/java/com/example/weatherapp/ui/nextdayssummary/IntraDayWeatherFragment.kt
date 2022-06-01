package com.example.weatherapp.ui.nextdayssummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.todayoverview.HourlyListAdapter
import com.example.weatherapp.databinding.FragmentIntraDayWeatherBinding
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel.Companion.getFeelsLikeTempPref
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel.Companion.getPressurePref
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel.Companion.getTempPref
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

class IntraDayWeatherFragment : Fragment() {

    private lateinit var binding: FragmentIntraDayWeatherBinding
    private lateinit var adapter: HourlyListAdapter
    private val nextDaysViewModel: NextDaysViewModel by viewModels()
    val formatter = DateTimeFormatter.ofPattern("dd MMM")
    private var currentDayData: List<DayWeatherModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentIntraDayWeatherBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        nextDaysViewModel.getNextDaysWeather()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = requireArguments().getInt("itemIndex")
        observe(index)
        setupRecycleView()
    }

    private fun observe(index: Int) {
        nextDaysViewModel.selectedCardIndex.observe(viewLifecycleOwner) {
            adapter.selectedIndex = it
        }

        nextDaysViewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            currentDayData = it.first
            adapter.hourlyTempList = currentDayData[index].hourlyWeatherList
            setupDetailedCard(
                currentDayData[index].hourlyWeatherList[it.second],
                currentDayData[index].date
            )
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            nextDaysViewModel.updateSelectedCardIndex(it)
        }

        adapter = HourlyListAdapter(onItemClicked, false)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.scrollToPosition(LocalDateTimeImpl().getDateTime().hour)
    }

    private fun setupDetailedCard(hourWeatherObj: HourWeatherModel, dayDate: LocalDate) {
        val dayOfMonth = dayDate.dayOfWeek.getDisplayName(
            TextStyle.FULL, Locale.getDefault()
        )
        val dateFormatted = dayDate.format(formatter)
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
            getTempPref(hourWeatherObj, true)
        )
        binding.windValue.text = getString(
            R.string.current_wind_speed,
            hourWeatherObj.windKph.toString(),
            "km/h"
        )
        binding.feelsLikeValue.text = getString(
            R.string.feels_like_temp,
            getFeelsLikeTempPref(hourWeatherObj, true)
        )
        binding.humidityValue.text = getString(
            R.string.humidity_value,
            hourWeatherObj.humidity.toString()
        )
        binding.pressureValue.text = getString(
            R.string.detailed_card_pressure,
            getPressurePref(hourWeatherObj)
        )
    }
}