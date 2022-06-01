package com.example.weatherapp.ui.todayoverview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.core.services.WeatherBroadcastReceiver
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.services.ForegroundService
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import com.example.weatherapp.databinding.FragmentTodayOverviewBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

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
    private lateinit var broadcast: BroadcastReceiver
    private val formatter = DateTimeFormatter.ofPattern("dd MMM")
    private val COMMAND = "location_update"
    private val weatherViewModel: DetailedWeatherViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcast = WeatherBroadcastReceiver()
        weatherViewModel.getWeatherData()
        LocalBroadcastManager.getInstance(context!!).registerReceiver(broadcast,
            IntentFilter(COMMAND)
        )
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!).unregisterReceiver(broadcast)
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
        binding.recyclerView.scrollToPosition(LocalDateTimeImpl().getDateTime().hour)
    }

    private fun observe() {
        weatherViewModel.cardIndexSelected.observe(viewLifecycleOwner) {
            adapter.selectedIndex = it
        }

        weatherViewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe
            setupDetailedCard(it)
            ForegroundService.stopService(context!!)
            ForegroundService.startService(context!!, createNotificationMessage(it))
        }

        weatherViewModel.todayWeatherData.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe
            adapter.hourlyTempList = it.hourlyWeatherList
            setupDateSubtitle(it.date)
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
                DetailedWeatherViewModel.getTempPref(hourWeatherObj, true)
            )
            windValue.text = getString(
                R.string.current_wind_speed,
                hourWeatherObj.windKph.toString(),
                "km/h"
            )
            feelsLikeValue.text = getString(
                R.string.feels_like_temp,
                DetailedWeatherViewModel.getFeelsLikeTempPref(hourWeatherObj, true)
            )
            humidityValue.text = getString(
                R.string.humidity_value,
                hourWeatherObj.humidity.toString()
            )
            pressureValue.text = getString(
                R.string.detailed_card_pressure,
                DetailedWeatherViewModel.getPressurePref(hourWeatherObj)
            )
        }
    }

    private fun createNotificationMessage(weather: HourWeatherModel): String {
        var summary = ""
        summary += "temp ${weather.tempC}°C | hum ${weather.humidity} | "
        summary += "feels_like ${weather.feelsLikeC}°C"

        return summary
    }

    private fun setupDateSubtitle(dayDate: LocalDate) {
        val dayOfMonth = dayDate.dayOfWeek.getDisplayName(
            TextStyle.FULL, Locale.getDefault()
        )
        val dateFormatted = dayDate.format(formatter)
        binding.todayDate.text = "${dayOfMonth}, ${dateFormatted}"
    }
}