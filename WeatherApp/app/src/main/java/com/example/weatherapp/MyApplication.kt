package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.core.dagger.DaggerApplicationComponent

class MyApplication : Application() {

    val appComponent = DaggerApplicationComponent.factory().create(this)
}