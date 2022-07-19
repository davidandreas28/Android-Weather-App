package com.example.weatherapp.ui.todayoverview

import com.example.weatherapp.core.repositories.LocationData
import com.example.weatherapp.core.repositories.LocationRepository
import com.example.weatherapp.core.utils.LocationProvider
import com.example.weatherapp.utils.TestDispatcherProviderImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class MainActivityViewModelTest {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    @Mock
    private lateinit var repository: LocationRepository

    @Mock
    private lateinit var locationProvider: LocationProvider

    @Before
    fun setUp() {
        val dispatcherProvider = TestDispatcherProviderImpl()
        MockitoAnnotations.openMocks(this)
        whenever(repository.locationData).thenReturn(
            flowOf()
        )
        Dispatchers.setMain(dispatcherProvider.Main())
        mainActivityViewModel =
            MainActivityViewModel(locationProvider, repository, dispatcherProvider)
    }

    @Test
    fun setLocationUpdate() = runBlocking {
        val testLatitude = 11.0
        val testLongitude = 10.0
        val testLocationData = LocationData(
            testLongitude,
            testLatitude,
            "Bucharest",
            "Romania"
        )

        whenever(locationProvider.provideLocation(testLatitude, testLongitude)).thenReturn(
            testLocationData
        )

        mainActivityViewModel.setLocation(testLatitude, testLongitude)
        verify(repository, times(1)).updateLocation(testLocationData)
    }
}