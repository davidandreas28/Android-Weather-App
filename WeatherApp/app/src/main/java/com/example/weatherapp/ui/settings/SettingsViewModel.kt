package com.example.weatherapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val preferencesLiveData = userPreferencesRepository.userPreferencesFlow.asLiveData()

    fun updateTempPref(celsiusPref: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateTempPref(celsiusPref)
        }
    }

    fun updatePressurePref(mbPref: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updatePressurePref(mbPref)
        }
    }
}