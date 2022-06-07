package com.example.weatherapp.core.datasources.local

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.core.models.Location

object LocationSharedPrefs {

    private const val LOCATION_SHARED_PREFS = "LOCATION_SHARED_PREFS"

    lateinit var sharedPrefs: SharedPreferences

    fun initSharedPrefs(context: Context) {
        sharedPrefs = context.applicationContext.getSharedPreferences(
            LOCATION_SHARED_PREFS,
            Context.MODE_PRIVATE
        )
    }

    fun saveLocation(location: Location){
        //write into sharedPrefs
        with(sharedPrefs.edit()) {
            putFloat("latitude", location.lat.toFloat())
            putFloat("longitude", location.lon.toFloat())
            putString("loc_name", "${location.city}, ${location.country}")
            apply()
        }
    }

    fun getLocation():Pair<Double, Double>{
        val latitude = sharedPrefs.getFloat("latitude", 51.50f)
        val longitude = sharedPrefs.getFloat("longitude",  -0.11f)
        return Pair(latitude.toDouble(), longitude.toDouble())
    }

    fun getLocationName(): String? {
        return sharedPrefs.getString("loc_name", "Unknown")
    }

}