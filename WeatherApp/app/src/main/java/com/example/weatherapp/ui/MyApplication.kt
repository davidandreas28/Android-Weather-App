package com.example.weatherapp.ui

import android.app.Application
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs
import com.example.weatherapp.core.datasources.local.SettingsSharedPrefs

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        LocationSharedPrefs.initSharedPrefs(this)
        SettingsSharedPrefs.initSharedPrefs(this)
    }
}