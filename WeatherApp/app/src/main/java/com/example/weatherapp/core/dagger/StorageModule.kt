package com.example.weatherapp.core.dagger

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.repositories.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class LocationStorage

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class SettingsStorage

@Module
class StorageModule {
    private val LOCATION_SHARED_PREFS = "LOCATION_SHARED_PREFS"
    private val SETTINGS_SHARED_PREFS = "SETTINGS_SHARED_PREFS"

    @LocationStorage
    @Provides
    @Singleton
    fun provideLocationDataStore(appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile(LOCATION_SHARED_PREFS)
            }
        )

    @Provides
    @Singleton
    fun provideLocationRepository(@LocationStorage dataStore: DataStore<Preferences>): LocationRepository {
        return LocationRepository(dataStore)
    }

    @SettingsStorage
    @Provides
    @Singleton
    fun providePrefDataStore(appContext: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = {
                appContext.preferencesDataStoreFile(SETTINGS_SHARED_PREFS)
            }
        )

    @Provides
    @Singleton
    fun providePrefRepository(@SettingsStorage dataStore: DataStore<Preferences>): UserPreferencesRepository {
        return UserPreferencesRepository(dataStore)
    }

}