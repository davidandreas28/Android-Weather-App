package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.core.datasources.local.SettingsSharedPrefs
import com.example.weatherapp.databinding.FragmentSettingsBinding
import com.example.weatherapp.ui.todayoverview.TodayOverviewFragment

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    interface ToolbarSetup {
        fun setupSettingsToolbar()
    }

    private val dummyLinker = object :
        SettingsFragment.ToolbarSetup {
        override fun setupSettingsToolbar() {}
    }

    private var mainActivityLinker: SettingsFragment.ToolbarSetup = dummyLinker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        initRadioPositions()
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainActivityLinker.setupSettingsToolbar()
        binding.tempRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.temp_radio_button_1 -> SettingsSharedPrefs.saveTempPrefs(true)
                else -> SettingsSharedPrefs.saveTempPrefs(false)
            }
        }

        binding.pressureRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.pressure_radio_button_1 -> SettingsSharedPrefs.savePressurepref(true)
                else -> SettingsSharedPrefs.savePressurepref(false)
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
        if (SettingsSharedPrefs.getTempPrefs() == true)
            binding.tempRadioButton1.isChecked = true
        else
            binding.tempRadioButton2.isChecked = true

        if (SettingsSharedPrefs.getPressurePref() == true)
            binding.pressureRadioButton1.isChecked = true
        else
            binding.pressureRadioButton2.isChecked = true
    }
}