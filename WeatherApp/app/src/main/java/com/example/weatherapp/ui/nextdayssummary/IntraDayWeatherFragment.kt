package com.example.weatherapp.ui.nextdayssummary

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.ui.todayoverview.HourlyListAdapter
import com.example.weatherapp.databinding.FragmentIntraDayWeatherBinding
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.repositories.UserPreferences
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import com.example.weatherapp.core.utils.Utils.Companion.getFeelsLikeTempPref
import com.example.weatherapp.core.utils.Utils.Companion.getPressurePref
import com.example.weatherapp.core.utils.Utils.Companion.getTempPref
import com.example.weatherapp.MyApplication
import com.example.weatherapp.ui.todayoverview.MainActivityViewModel
import java.util.*
import javax.inject.Inject

class IntraDayWeatherFragment : Fragment() {

    private lateinit var binding: FragmentIntraDayWeatherBinding
    private lateinit var adapter: HourlyListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val nextDaysViewModel: NextDaysViewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[NextDaysViewModel::class.java]
    }
    private var currentDayData: List<DayWeatherModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val fragmentBinding = FragmentIntraDayWeatherBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val index = requireArguments().getInt("itemIndex")
        observe(index)
        setupRecycleView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyApplication).appComponent.inject(this)
    }

    private fun observe(index: Int) {
        nextDaysViewModel.selectedCardIndex.observe(viewLifecycleOwner) {
            adapter.selectedIndex = it
        }

        nextDaysViewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it?.first == null || it.second == null || it.third == null) {
                return@observe
            }

            currentDayData = it.first!!
            adapter.hourlyTempList = currentDayData[index].hourlyWeatherList
            setupDetailedCard(
                currentDayData[index],
                it.second!!,
                it.third!!
            )
            adapter.userPreferences = it.third!!
            binding.recyclerView.scrollToPosition(it.second!!)
        }

        nextDaysViewModel.locationData.observe(viewLifecycleOwner) {
            nextDaysViewModel.provideWeatherData(it)
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
    }

    private fun setupDetailedCard(
        dayWeatherObj: DayWeatherModel,
        dayPos: Int,
        userPreferences: UserPreferences
    ) {
        val hourWeatherObj = dayWeatherObj.hourlyWeatherList[dayPos]

        binding.todayElement.text = dayWeatherObj.getFormattedDate()
        binding.detailedCardTimeframe.text = getString(
            R.string.detailed_card_time,
            hourWeatherObj.time
        )
        binding.mainWeatherIcon.setImageResource(
            hourWeatherObj.weatherType.imgSrc
        )
        binding.mainTemperatureValue.text = getString(
            R.string.current_temp,
            getTempPref(hourWeatherObj, userPreferences, true)
        )
        binding.windValue.text = getString(
            R.string.current_wind_speed,
            hourWeatherObj.windKph.toString(),
            "km/h"
        )
        binding.feelsLikeValue.text = getString(
            R.string.feels_like_temp,
            getFeelsLikeTempPref(hourWeatherObj, userPreferences, true)
        )
        binding.humidityValue.text = getString(
            R.string.humidity_value,
            hourWeatherObj.humidity.toString()
        )
        binding.pressureValue.text = getString(
            R.string.detailed_card_pressure,
            getPressurePref(hourWeatherObj, userPreferences)
        )
    }
}