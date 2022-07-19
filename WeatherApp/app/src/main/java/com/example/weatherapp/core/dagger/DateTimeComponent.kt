package com.example.weatherapp.core.dagger

import com.example.weatherapp.core.utils.DateTime
import com.example.weatherapp.core.utils.LocalDateTimeImpl
import dagger.Module
import dagger.Provides

@Module
class DateTimeComponent {
    @Provides
    fun provideLocalDateTime(): DateTime {
        return LocalDateTimeImpl
    }
}