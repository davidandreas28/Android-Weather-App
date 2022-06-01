package com.example.weatherapp.core.datasources.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val serviceConfig = ServiceConfig()
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(serviceConfig.BASE_URL)
    .build()

interface WeatherApiService {
    @GET("v1/forecast.json")
    suspend fun getWeatherData(
        @Query("key") API: String,
        @Query("q") q: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherApiModel
}

object WeatherApi {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}