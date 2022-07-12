package com.example.weatherapp

import android.app.Application
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import com.example.weatherapp.core.dagger.DaggerApplicationComponent
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase.Companion.getDatabase

class MyApplication : Application() {

    val database: WeatherDatabase by lazy { getDatabase(this) }
    val appComponent = DaggerApplicationComponent.factory().create(this)
}