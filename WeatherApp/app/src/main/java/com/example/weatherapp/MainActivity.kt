package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.weatherapp.core.models.Location
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.nextdayssummary.NextDaysFragment
import com.example.weatherapp.ui.todayoverview.TodayOverviewFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), NextDaysFragment.OnItemClickedListener,
    TodayOverviewFragment.MainActivityLinker {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSION_ID = 44

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel.location.observe(this, updateLocation)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocation()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<TodayOverviewFragment>(R.id.fragment_container_view)
            }
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            }
        }
    }

    override fun onBackButtonClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNextDaysButtonClicked() {
        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace<NextDaysFragment>(R.id.fragment_container_view)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun setHomeAsUp() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun requestLocation() {
        if (!checkPermission()) {
            requirePermissions()
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable location.", Toast.LENGTH_LONG).show();
            val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        fusedLocationProviderClient.lastLocation.addOnCompleteListener {
            val location = it.result
            viewModel.setLocation(this, location.latitude, location.longitude)
        }
    }

    private fun requirePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID
        );
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        );
    }

    private val updateLocation: (Location) -> Unit = { newLocation ->
        val updatedToolbarTitle = newLocation.asString()
        binding.toolbar.title = updatedToolbarTitle
    }
}