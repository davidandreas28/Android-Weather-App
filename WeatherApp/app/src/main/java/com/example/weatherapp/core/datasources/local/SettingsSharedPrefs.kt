package com.example.weatherapp.core.datasources.local

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.core.models.Location

object SettingsSharedPrefs {
    private const val LOCATION_SHARED_PREFS = "SETTINGS_SHARED_PREFS"

    lateinit var sharedPrefs: SharedPreferences

    fun initSharedPrefs(context: Context) {
        sharedPrefs = context.applicationContext.getSharedPreferences(
            SettingsSharedPrefs.LOCATION_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    fun saveTempPrefs(celsiusPref: Boolean){
        // if celsiusPref is false, then fahrenheit  is used
        with(sharedPrefs.edit()) {
            putBoolean("celsius_pref", celsiusPref)
            apply()
        }
    }

    fun getTempPrefs(): Boolean{
        val tempPref = sharedPrefs.getBoolean("celsius_pref",  true)
        return tempPref
    }

    fun savePressurepref(mbPref: Boolean){
        // if mbPref is false, then in is used
        with(sharedPrefs.edit()) {
            putBoolean("mb_pref", mbPref)
            apply()
        }
    }

    fun getPressurePref(): Boolean{
        val pressurePref = sharedPrefs.getBoolean("mb_pref",  true)
        return pressurePref
    }

}