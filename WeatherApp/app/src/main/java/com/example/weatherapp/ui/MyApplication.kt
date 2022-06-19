package com.example.weatherapp.ui

import android.app.Application
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.local.SettingsSharedPrefs
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase.Companion.getDatabase

class MyApplication : Application() {
    val database: WeatherDatabase by lazy { getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()

        LocationSharedPrefs.initSharedPrefs(this)
        SettingsSharedPrefs.initSharedPrefs(this)
    }
}