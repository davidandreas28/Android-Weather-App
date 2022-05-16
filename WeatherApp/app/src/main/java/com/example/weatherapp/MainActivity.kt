package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.weatherapp.viewmodels.DetailedWeatherViewModel

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (savedInstanceState == null) {
      supportFragmentManager.commit {
        setReorderingAllowed(true)
        add<TodayOverviewFragment>(R.id.fragment_container_view)
      }
    }

    val modelDetailed: DetailedWeatherViewModel by viewModels()

    modelDetailed.currentCity.observe(this) {
      modelDetailed.buildAppBarTitle(
        modelDetailed.currentCity.value.toString(),
        modelDetailed.currentCountry.value.toString()
      )
    }
    modelDetailed.currentCountry.observe(this) {
      modelDetailed.buildAppBarTitle(
        modelDetailed.currentCity.value.toString(),
        modelDetailed.currentCountry.value.toString()
      )
    }
    modelDetailed.currentLocation.observe(this) {
      supportActionBar?.title = getString(
        R.string.app_bar_title, modelDetailed.currentLocation.value.toString()
      )
    }
  }
}