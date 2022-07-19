package com.example.weatherapp.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider  {
    fun IO(): CoroutineDispatcher
    fun Main(): CoroutineDispatcher
    fun Default(): CoroutineDispatcher
}

class DispatcherProviderImpl : DispatcherProvider {
    override fun IO() = Dispatchers.IO

    override fun Main() = Dispatchers.Main

    override fun Default() = Dispatchers.Default
}

class TestDispatcherProviderImpl : DispatcherProvider {
    override fun IO() = Dispatchers.Unconfined

    override fun Main() = Dispatchers.Unconfined

    override fun Default() = Dispatchers.Unconfined

}