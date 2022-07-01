package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import kotlinx.coroutines.launch

class SettingsViewModel(val userPreferencesRepository: UserPreferencesRepository) : ViewModel() {
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

    val preferencesLiveData = userPreferencesRepository.userPreferencesFlow.asLiveData()
}

class SettingsViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}