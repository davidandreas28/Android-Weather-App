package com.example.weatherapp.core.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.telecom.TelecomManager
import android.util.Log
import android.widget.Toast

class WeatherBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val locationResp = p1?.extras?.get(TelecomManager.EXTRA_LOCATION) as Location
        Log.i("BROADCAST_RECEIVED", "${locationResp.latitude}, ${locationResp.longitude}")
        Toast.makeText(
            p0,
            "${locationResp.latitude}, ${locationResp.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }
}