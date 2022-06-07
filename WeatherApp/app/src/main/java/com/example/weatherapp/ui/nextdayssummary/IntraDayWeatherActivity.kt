package com.example.weatherapp.ui.nextdayssummary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.weatherapp.R
import com.example.weatherapp.core.datasources.local.LocationSharedPrefs


class IntraDayWeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intra_day_weather)
        val index: Int = intent.getIntExtra("itemIndex", 0)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val bundle = bundleOf("itemIndex" to index)
                setReorderingAllowed(true)
                add<IntraDayWeatherFragment>(R.id.fragment_container_view, args = bundle)
            }
        }

        setupActionBar()
    }

    fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        supportActionBar?.title = LocationSharedPrefs.getLocationName()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_down, R.anim.slide_down_2)
        }
    }


}