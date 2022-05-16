package com.example.weatherapp.models

import com.example.weatherapp.R
import kotlin.random.Random

data class HourlyTemperatureModel(val hour: String) {
    var iconSrc: Int
    var temp: Int

    init {
        iconSrc = R.drawable.ic_drizzle
        temp = Random.nextInt(-20, 35)
    }
}