package com.example.weatherapp.core.dagger

import android.content.Context
import com.example.weatherapp.ui.nextdayssummary.IntraDayWeatherActivity
import com.example.weatherapp.ui.nextdayssummary.IntraDayWeatherFragment
import com.example.weatherapp.ui.nextdayssummary.NextDaysFragment
import com.example.weatherapp.ui.settings.SettingsFragment
import com.example.weatherapp.ui.todayoverview.MainActivity
import com.example.weatherapp.ui.todayoverview.TodayOverviewFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FusedModule::class, ViewModelModule::class, StorageModule::class, DatabaseModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: TodayOverviewFragment)
    fun inject(activity: SettingsFragment)
    fun inject(activity: NextDaysFragment)
    fun inject(activity: IntraDayWeatherActivity)
    fun inject(activity: IntraDayWeatherFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}