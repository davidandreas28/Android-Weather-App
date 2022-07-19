package com.example.weatherapp.core.dagger

import android.content.Context
import com.example.weatherapp.core.utils.LocationProvider
import com.example.weatherapp.core.utils.LocationProviderImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides

@Module
class LocationModule {
    @Provides
    fun provideFuseLocationClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideLocationProvider(context: Context): LocationProvider {
        return LocationProviderImpl(context)
    }
}