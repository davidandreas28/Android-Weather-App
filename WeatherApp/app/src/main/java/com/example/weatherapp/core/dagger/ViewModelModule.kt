package com.example.weatherapp.core.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.nextdayssummary.NextDaysViewModel
import com.example.weatherapp.ui.settings.SettingsViewModel
import com.example.weatherapp.ui.todayoverview.DetailedWeatherViewModel
import com.example.weatherapp.ui.todayoverview.MainActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    internal abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NextDaysViewModel::class)
    internal abstract fun bindNextDaysViewModel(nextDaysViewModel: NextDaysViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailedWeatherViewModel::class)
    internal abstract fun bindDetailedWeatherViewModel(detailedWeatherViewModel: DetailedWeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    internal abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}