package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import com.example.weatherapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by activityViewModels {
        SettingsViewModelFactory(
            UserPreferencesRepository((activity?.application as MyApplication).dataStorePref)
        )
    }

    interface ToolbarSetup {
        fun setupSettingsToolbar()
    }

    private val dummyLinker = object :
        ToolbarSetup {
        override fun setupSettingsToolbar() {}
    }

    private var mainActivityLinker: ToolbarSetup = dummyLinker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        initRadioPositions()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivityLinker.setupSettingsToolbar()
        binding.tempRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.temp_celsius_radio -> viewModel.updateTempPref(true)
                else -> viewModel.updateTempPref(false)
            }
        }

        binding.pressureRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.pressure_millibars_radio -> viewModel.updatePressurePref(true)
                else -> viewModel.updatePressurePref(false)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivityLinker = context
        }
    }

    private fun initRadioPositions() {
        viewModel.preferencesLiveData.observe(this) {
            if (it.celsiusTempPref) {
                binding.tempCelsiusRadio.isChecked = true
            } else {
                binding.tempFahrenheitRadio.isChecked = true
            }
            if (it.mbPressurePref) {
                binding.pressureMillibarsRadio.isChecked = true
            } else {
                binding.pressureMercuryRadio.isChecked = true
            }
        }
    }
}