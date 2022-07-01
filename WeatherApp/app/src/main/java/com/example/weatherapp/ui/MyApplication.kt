package com.example.weatherapp.ui

import android.app.Application
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase.Companion.getDatabase

class MyApplication : Application() {
    private val SETTINGS_SHARED_PREFS = "SETTINGS_SHARED_PREFS"
    private val LOCATION_SHARED_PREFS = "LOCATION_SHARED_PREFS"
    val database: WeatherDatabase by lazy { getDatabase(this) }

    val dataStorePref by preferencesDataStore(
        name = SETTINGS_SHARED_PREFS,
        produceMigrations = {
            listOf(SharedPreferencesMigration(it, SETTINGS_SHARED_PREFS))
        }
    )

    val dataStoreLocation by preferencesDataStore(
        name = LOCATION_SHARED_PREFS,
        produceMigrations = {
            listOf(SharedPreferencesMigration(it, LOCATION_SHARED_PREFS))
        }
    )
}