package com.example.weatherapp.core.dagger

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.core.datasources.local.databases.WeatherDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            "weather"
        ).build()
    }
}