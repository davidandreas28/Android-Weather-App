package com.example.weatherapp.core.datasources.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.weatherapp.asString
import com.example.weatherapp.core.models.Location

object LocationSharedPrefs {

    private const val LOCATION_SHARED_PREFS = "LOCATION_SHARED_PREFS"
    private const val DEFAULT_LATITUDE = 34.052235f
    private const val DEFAULT_LONGITUDE = -118.243683f
    private const val DEFAULT_LOCATION = "Los Angeles, United States"


    lateinit var sharedPrefs: SharedPreferences

    fun initSharedPrefs(context: Context) {
        sharedPrefs = context.applicationContext.getSharedPreferences(
            LOCATION_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    fun saveLocation(location: Location) {
        //write into sharedPrefs
        with(sharedPrefs.edit()) {
            Log.i("LOCATION_PREF_ACTION", "saving ${location.asString()}")
            putFloat("latitude", location.lat.toFloat())
            putFloat("longitude", location.lon.toFloat())
            putString("loc_name", "${location.city}, ${location.country}")
            apply()
        }
    }

    fun getLocation(): Pair<Double, Double> {
        val latitude = sharedPrefs.getFloat("latitude", DEFAULT_LATITUDE)
        val longitude = sharedPrefs.getFloat("longitude", DEFAULT_LONGITUDE)
        return Pair(latitude.toDouble(), longitude.toDouble())
    }

    fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun getLocationName(): String? {
        return sharedPrefs.getString("loc_name", DEFAULT_LOCATION)
    }

}