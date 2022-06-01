package com.example.weatherapp.core.models

import com.example.weatherapp.R

enum class WeatherType(val imgSrc: Int) {
    SUNNY(R.drawable.ic_drizzle),
    PARTLY_CLOUDY(R.drawable.ic_drizzle),
    CLOUDY(R.drawable.ic_drizzle),
    OVERCAST(R.drawable.ic_drizzle),
    MIST(R.drawable.ic_drizzle),
    PATCHY_RAIN_POSSIBLE(R.drawable.ic_drizzle),
    PATCHY_SNOW_POSSIBLE(R.drawable.ic_drizzle),
    PATCHY_SLEET_POSSIBLE(R.drawable.ic_drizzle),
    PATCHY_FREEZING_DRIZZLE_POSSIBLE(R.drawable.ic_drizzle),
    THUNDERY_OUTBREAKS_POSSIBLE(R.drawable.ic_drizzle),
    BLOWING_SNOW(R.drawable.ic_drizzle),
    BLIZZARD(R.drawable.ic_drizzle),
    FOG(R.drawable.ic_drizzle),
    CLEAR(R.drawable.ic_drizzle),
    FREEZING_FOG(R.drawable.ic_drizzle),
    PATCHY_LIGHT_DRIZZLE(R.drawable.ic_drizzle),
    LIGHT_DRIZZLE(R.drawable.ic_drizzle),
    FREEZING_DRIZZLE(R.drawable.ic_drizzle),
    HEAVY_FREEZING_DRIZZLE(R.drawable.ic_drizzle),
    PATCHY_LIGHT_RAIN(R.drawable.ic_drizzle),
    LIGHT_RAIN(R.drawable.ic_drizzle),
    MODERATE_RAIN_AT_TIMES(R.drawable.ic_drizzle),
    MODERATE_RAIN(R.drawable.ic_drizzle),
    HEAVY_RAIN_AT_TIMES(R.drawable.ic_drizzle),
    HEAVY_RAIN(R.drawable.ic_drizzle),
    LIGHT_FREEZING_RAIN(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_FREEZING_RAIN(R.drawable.ic_drizzle),
    LIGHT_SLEET(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_SLEET(R.drawable.ic_drizzle),
    PATCHY_LIGHT_SNOW(R.drawable.ic_drizzle),
    LIGHT_SNOW(R.drawable.ic_drizzle),
    PATCHY_MODERATE_SNOW(R.drawable.ic_drizzle),
    MODERATE_SNOW(R.drawable.ic_drizzle),
    PATCHY_HEAVY_SNOW(R.drawable.ic_drizzle),
    HEAVY_SNOW(R.drawable.ic_drizzle),
    ICE_PELLETS(R.drawable.ic_drizzle),
    LIGHT_RAIN_SHOWER(R.drawable.ic_drizzle),
    TORRENTIAL_RAIN_SHOWER(R.drawable.ic_drizzle),
    LIGHT_SLEET_SHOWERS(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_SLEET_SHOWERS(R.drawable.ic_drizzle),
    LIGHT_SNOW_SHOWERS(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_SNOW_SHOWERS(R.drawable.ic_drizzle),
    LIGHT_SHOWERS_OF_ICE_PALLETS(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_SHOWERS_OF_ICE_PALLETS(R.drawable.ic_drizzle),
    PATCHY_LIGHT_RAIN_WITH_TUNDER(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_RAIN_WITH_THUNDER(R.drawable.ic_drizzle),
    PATCHY_LIGHT_SNOW_WITH_THUNDER(R.drawable.ic_drizzle),
    MODERATE_OR_HEAVY_SNOW_WITH_THUNDER(R.drawable.ic_drizzle);
}