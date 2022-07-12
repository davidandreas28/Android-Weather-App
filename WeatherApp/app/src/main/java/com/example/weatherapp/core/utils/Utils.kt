package com.example.weatherapp.core.utils

import com.example.weatherapp.core.models.HourWeatherModel
import com.example.weatherapp.core.repositories.UserPreferences

class Utils {
    companion object {
        fun getTempPref(
            hourWeatherObj: HourWeatherModel,
            userPreferences: UserPreferences,
            ending: Boolean
        ): String {
            return if (userPreferences.celsiusTempPref)
                "${hourWeatherObj.tempC}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.tempF}${if (ending) "°F" else "°"}"
        }

        fun getFeelsLikeTempPref(
            hourWeatherObj: HourWeatherModel,
            userPreferences: UserPreferences,
            ending: Boolean
        ): String {
            return if (userPreferences.celsiusTempPref)
                "${hourWeatherObj.feelsLikeC}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.feelsLikeF}${if (ending) "°F" else "°"}"
        }

        fun getPressurePref(
            hourWeatherObj: HourWeatherModel,
            userPreferences: UserPreferences
        ): String {
            return if (userPreferences.mbPressurePref)
                "${hourWeatherObj.pressureMb} mb"
            else
                "${hourWeatherObj.pressureIn} in"
        }

        fun getMaxTempPref(
            hourWeatherObj: HourWeatherModel,
            userPreferences: UserPreferences,
            ending: Boolean
        ): String {
            return if (userPreferences.celsiusTempPref)
                "${hourWeatherObj.maxTempC.toInt()}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.maxTempF.toInt()}${if (ending) "°F" else "°"}"
        }

        fun getMinTempPref(
            hourWeatherObj: HourWeatherModel,
            userPreferences: UserPreferences,
            ending: Boolean
        ): String {
            return if (userPreferences.celsiusTempPref)
                "${hourWeatherObj.minTempC.toInt()}${if (ending) "°C" else "°"}"
            else
                "${hourWeatherObj.minTempF.toInt()}${if (ending) "°F" else "°"}"
        }
    }
}