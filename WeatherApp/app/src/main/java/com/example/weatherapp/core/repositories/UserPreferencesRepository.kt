package com.example.weatherapp.core.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.weatherapp.core.repositories.PreferencesKeys.CELSIUS_PREF
import com.example.weatherapp.core.repositories.PreferencesKeys.MB_PREF
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserPreferences(val celsiusTempPref: Boolean, val mbPressurePref: Boolean)

private object PreferencesKeys {
    val CELSIUS_PREF = booleanPreferencesKey("celsius_pref")
    val MB_PREF = booleanPreferencesKey("mb_pref")
}

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val tempPref = preferences[CELSIUS_PREF] ?: true
            val mbPref = preferences[MB_PREF] ?: true

            UserPreferences(tempPref, mbPref)
        }

    suspend fun updateTempPref(tempPrefValue: Boolean) {
        dataStore.edit { preferences ->
            preferences[CELSIUS_PREF] = tempPrefValue
        }
    }

    suspend fun updatePressurePref(pressurePrefValue: Boolean) {
        dataStore.edit { preferences ->
            preferences[MB_PREF] = pressurePrefValue
        }
    }
}