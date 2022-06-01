package com.example.weatherapp.core.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class WeatherBroadcastReceiver : BroadcastReceiver() {
    val EXTRA_NAME = "message"

    override fun onReceive(p0: Context?, p1: Intent?) {
        var message = "Weather data requested. Today forecast -  "
        message += p1?.getStringExtra(EXTRA_NAME)
        Toast.makeText(p0, message, Toast.LENGTH_SHORT).show()
    }
}