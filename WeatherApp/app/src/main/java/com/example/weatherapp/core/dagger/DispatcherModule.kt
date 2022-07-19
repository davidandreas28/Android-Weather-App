package com.example.weatherapp.core.dagger

import com.example.weatherapp.utils.DispatcherProvider
import com.example.weatherapp.utils.DispatcherProviderImpl
import dagger.Module
import dagger.Provides

@Module
class DispatcherModule {
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return DispatcherProviderImpl()
    }
}