package com.example.weatherapp.core.datasources.local

import android.content.Context
import android.content.SharedPreferences

object SettingsSharedPrefs {
    private const val SETTINGS_SHARED_PREFS = "SETTINGS_SHARED_PREFS"

    lateinit var sharedPrefs: SharedPreferences

    fun initSharedPrefs(context: Context) {
        sharedPrefs = context.applicationContext.getSharedPreferences(
            SETTINGS_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    fun saveTempPrefs(celsiusPref: Boolean) {
        // if celsiusPref is false, then fahrenheit  is used
        with(sharedPrefs.edit()) {
            putBoolean("celsius_pref", celsiusPref)
            apply()
        }
    }

    fun getTempPrefs(): Boolean {
        return sharedPrefs.getBoolean("celsius_pref", true)
    }

    fun savePressurepref(mbPref: Boolean) {
        // if mbPref is false, then inPref is used
        with(sharedPrefs.edit()) {
            putBoolean("mb_pref", mbPref)
            apply()
        }
    }

    fun getPressurePref(): Boolean {
        return sharedPrefs.getBoolean("mb_pref", true)
    }

}