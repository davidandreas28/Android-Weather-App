package com.example.weatherapp.ui.todayoverview

import android.content.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherapp.core.models.DayWeatherModel
import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.repositories.UserPreferences
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import com.example.weatherapp.core.repositories.asString
import com.example.weatherapp.databinding.FragmentTodayOverviewBinding
import com.example.weatherapp.MyApplication
import com.example.weatherapp.R
import com.example.weatherapp.core.utils.Utils.Companion.getFeelsLikeTempPref
import com.example.weatherapp.core.utils.Utils.Companion.getPressurePref
import com.example.weatherapp.core.utils.Utils.Companion.getTempPref
import java.util.*
import javax.inject.Inject

class TodayOverviewFragment : Fragment() {

    interface MainActivityLinker {
        fun onNextDaysButtonClicked()
        fun setHomeAsUp(title: String)
    }

    private val dummyLinker: MainActivityLinker = object : MainActivityLinker {
        override fun onNextDaysButtonClicked() {}
        override fun setHomeAsUp(title: String) {}
    }

    private lateinit var binding: FragmentTodayOverviewBinding
    private lateinit var adapter: HourlyListAdapter

    private var mainActivityLinker: MainActivityLinker = dummyLinker

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: DetailedWeatherViewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[DetailedWeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.deleteExpiredData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val fragmentBinding = FragmentTodayOverviewBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        setupRecycleView()

        binding.outlinedButton.setOnClickListener {
            mainActivityLinker.onNextDaysButtonClicked()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().application as MyApplication).appComponent.inject(this)
        if (context is MainActivity) {
            mainActivityLinker = context
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            viewModel.updateSelectedCardIndex(it)
        }

        adapter = HourlyListAdapter(
            onItemClicked,
            true
        )

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }

    private fun observe() {

        viewModel.networkStatus.observe(viewLifecycleOwner) {
            if (it == 1) {
                setMainComponentsVisibility(INVISIBLE)
                setBufferingSpinnerVisibility(true)
                setNetworkErrorElementVisibility(false)
            }

            if (it == 0) {
                setMainComponentsVisibility(VISIBLE)
                setBufferingSpinnerVisibility(false)
                setNetworkErrorElementVisibility(false)
            }

            if (it == -1) {
                setMainComponentsVisibility(INVISIBLE)
                setBufferingSpinnerVisibility(false)
                setNetworkErrorElementVisibility(true)
            }
        }

        viewModel.cardIndexSelected.observe(viewLifecycleOwner) {
            adapter.selectedIndex = it
        }

        viewModel.todayBigCardData.observe(viewLifecycleOwner) {
            if (it?.first == null || it.second == null || it.third == null)
                return@observe
            setupDetailedCard(it.first!!.hourlyWeatherList[it.second!!], it.third!!)
            binding.recyclerView.scrollToPosition(it.second!!)
            adapter.userPreferences = it.third!!
        }

        viewModel.locationData.observe(viewLifecycleOwner) {
            viewModel.provideWeatherData(it)
            mainActivityLinker.setHomeAsUp(it.asString())
        }

        viewModel.todayWeatherData.observe(viewLifecycleOwner) {
            if (it == null)
                return@observe
            adapter.hourlyTempList = it.hourlyWeatherList
            setupDateSubtitle(it)
        }
    }

    private fun setBufferingSpinnerVisibility(visible: Boolean) {
        if (visible) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = INVISIBLE
        }
    }

    private fun setNetworkErrorElementVisibility(visible: Boolean) {
        if (visible) {
            binding.errorText.visibility = VISIBLE
        } else {
            binding.errorText.visibility = INVISIBLE
        }
    }

    private fun setMainComponentsVisibility(visibility: Int) {
        val views = mutableListOf<View>()
        views.apply {
            add(binding.todayElement)
            add(binding.todayDate)
            add(binding.outlinedButton)
            add(binding.card)
            add(binding.recyclerView)
        }

        views.map {
            it.visibility = visibility
        }
    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        binding.swipeRefreshLayout.isRefreshing = true
        // call api to reload the screen
        viewModel.lastKnownLocation?.let {
            viewModel.provideWeatherData(it)
        }
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun setupDetailedCard(
        hourWeatherObj: HourWeatherModel,
        userPreferences: UserPreferences
    ) {

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
                getTempPref(hourWeatherObj, userPreferences, true)
            )
            windValue.text = getString(
                R.string.current_wind_speed,
                hourWeatherObj.windKph.toString(),
                "km/h"
            )
            feelsLikeValue.text = getString(
                R.string.feels_like_temp,
                getFeelsLikeTempPref(hourWeatherObj, userPreferences, true)
            )
            humidityValue.text = getString(
                R.string.humidity_value,
                hourWeatherObj.humidity.toString()
            )
            pressureValue.text = getString(
                R.string.detailed_card_pressure,
                getPressurePref(hourWeatherObj, userPreferences)
            )
        }
    }

    private fun setupDateSubtitle(dayWeatherObj: DayWeatherModel) {
        binding.todayDate.text = dayWeatherObj.getFormattedDate()
    }
}