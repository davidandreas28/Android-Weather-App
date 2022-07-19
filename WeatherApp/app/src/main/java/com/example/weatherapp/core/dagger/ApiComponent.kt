package com.example.weatherapp.core.dagger

import com.example.weatherapp.core.datasources.remote.WeatherApi
import com.example.weatherapp.core.datasources.remote.WeatherApiImpl
import dagger.Module
import dagger.Provides

@Module
class ApiComponent {
    @Provides
    fun provideWeatherApi(): WeatherApi {
        return WeatherApiImpl
    }
}