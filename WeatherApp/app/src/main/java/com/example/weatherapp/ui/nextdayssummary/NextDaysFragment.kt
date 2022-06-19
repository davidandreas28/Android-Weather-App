package com.example.weatherapp.ui.nextdayssummary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.databinding.FragmentNextDaysBinding
import com.example.weatherapp.MainActivity

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.ui.MyApplication
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModelFactory


class NextDaysFragment : Fragment() {

    interface OnItemClickedListener {
        fun onBackButtonClicked(item: MenuItem): Boolean
        fun setupToolbar()
    }

    private val sDummyCallbacks: OnItemClickedListener = object : OnItemClickedListener {
        override fun onBackButtonClicked(item: MenuItem): Boolean {
            return false
        }

        override fun setupToolbar() {
            return
        }
    }
    private var onClickListener: OnItemClickedListener = sDummyCallbacks
    private lateinit var adapter: NextDaysAdapter
    private lateinit var binding: FragmentNextDaysBinding
    private val nextDaysViewModel: NextDaysViewModel by viewModels {
        NextDaysViewModelModelFactory(
            (activity?.application as MyApplication).database
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        onClickListener.setupToolbar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentNextDaysBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nextDaysViewModel.provideWeatherData(context!!)
        setupRecycleView()
        observe()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return onClickListener.onBackButtonClicked(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            onClickListener = context
        }
    }

    private fun observe() {
        nextDaysViewModel.nextDaysData.observe(viewLifecycleOwner) {
            adapter.weatherData = it
        }
    }

    private fun setupRecycleView() {
        val onItemClicked: (Int) -> Unit = {
            val intent = Intent(context, IntraDayWeatherActivity::class.java)
            intent.putExtra("itemIndex", it)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        }

        adapter = NextDaysAdapter(
            onItemClicked
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
    }
}

class NextDaysViewModelModelFactory(private val database: WeatherDatabase) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NextDaysViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NextDaysViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}