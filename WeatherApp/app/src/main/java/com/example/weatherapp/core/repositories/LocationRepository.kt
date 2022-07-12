package com.example.weatherapp.core.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class LocationData(val lon: Double, val lat: Double, val city: String, val country: String)

fun LocationData.asString(): String {
    return "$city, $country"
}

private object LocationPreferencesKeys {
    val LATITUDE = doublePreferencesKey("latitude")
    val LONGITUDE = doublePreferencesKey("longitude")
    val CITY_NAME = stringPreferencesKey("city")
    val COUNTRY_NAME = stringPreferencesKey("country")
}

private const val DEFAULT_LATITUDE: Double = 34.052235
private const val DEFAULT_LONGITUDE: Double = -118.243683
private const val DEFAULT_CITY = "Los Angeles"
private const val DEFAULT_COUNTRY = "United States"

class LocationRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    val locationData: Flow<LocationData> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val latitude = preferences[LocationPreferencesKeys.LATITUDE] ?: DEFAULT_LATITUDE
            val longitude = preferences[LocationPreferencesKeys.LONGITUDE] ?: DEFAULT_LONGITUDE
            val city = preferences[LocationPreferencesKeys.CITY_NAME] ?: DEFAULT_CITY
            val country = preferences[LocationPreferencesKeys.COUNTRY_NAME] ?: DEFAULT_COUNTRY

            LocationData(latitude, longitude, city, country)
        }

    suspend fun updateLocation(location: LocationData) {
        dataStore.edit { preferences ->
            preferences[LocationPreferencesKeys.LATITUDE] = location.lat
            preferences[LocationPreferencesKeys.LONGITUDE] = location.lon
            preferences[LocationPreferencesKeys.CITY_NAME] = location.city
            preferences[LocationPreferencesKeys.COUNTRY_NAME] = location.country
        }
    }
}