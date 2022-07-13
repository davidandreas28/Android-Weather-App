package com.example.weatherapp.ui.todayoverview

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.ui.settings.SettingsFragment
import com.example.weatherapp.ui.nextdayssummary.NextDaysFragment
import com.google.android.gms.location.FusedLocationProviderClient

import com.example.weatherapp.core.services.LocationUpdatesService

import android.os.IBinder

import android.location.Location
import android.telecom.TelecomManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weatherapp.MyApplication
import com.example.weatherapp.R
import com.example.weatherapp.core.repositories.asString
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NextDaysFragment.OnItemClickedListener,
    TodayOverviewFragment.MainActivityLinker, SettingsFragment.ToolbarSetup {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[MainActivityViewModel::class.java]
    }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private var mService: LocationUpdatesService? = null
    private var mBound: Boolean = false
    lateinit var myReceiver: BroadcastReceiver

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
                requestLocation()
            } else {
                Toast.makeText(this, "Location cannot be determined.", Toast.LENGTH_SHORT).show()
            }
        }

    // Monitors the state of the connection to the service.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder: LocationUpdatesService.LocalBinder =
                service as LocationUpdatesService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        myReceiver = MyReceiver()
        val view = binding.root
        setContentView(view)

        LocationUpdatesService.startService(applicationContext)
        setSupportActionBar(binding.toolbar)
        setupBottomNavigation()
        requestLocation()
        observe()

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<TodayOverviewFragment>(R.id.fragment_container_view)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationUpdatesService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            myReceiver,
            IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(myReceiver)
        super.onPause()
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
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

    override fun setupToolbar(title: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = title
    }

    override fun setupSettingsToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = "Settings"
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

    override fun setHomeAsUp(title: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = title
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationUpdatesService.stopService(applicationContext)
    }

    private fun observe() {
        viewModel.location.observe(this) { location ->
            if (supportActionBar?.title != "Settings") {
                supportActionBar?.title = location.asString()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    supportFragmentManager.commit {
                        setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.fade_out_right,
                            R.anim.fade_in,
                            R.anim.slide_out
                        )
                        setReorderingAllowed(true)
                        supportFragmentManager.popBackStack(0, POP_BACK_STACK_INCLUSIVE)
                        replace<TodayOverviewFragment>(R.id.fragment_container_view)
                    }
                    true
                }
                R.id.settings -> {
                    supportFragmentManager.commit {
                        setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out
                        )
                        supportFragmentManager.popBackStack(0, POP_BACK_STACK_INCLUSIVE)
                        setReorderingAllowed(true)
                        replace<SettingsFragment>(R.id.fragment_container_view)
                    }
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                }
                R.id.settings -> {
                }
                else -> {
                }
            }
        }
    }

    private fun requestLocation() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                if (!isLocationEnabled()) {
                    val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent)
                }

                fusedLocationProviderClient.lastLocation.addOnCompleteListener {
                    val location = it.result
                    if (location != null) {
                        viewModel.setLocation(this, location.latitude, location.longitude)
                    }
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location: Location =
                intent.extras?.get(TelecomManager.EXTRA_LOCATION) as Location
            viewModel.setLocation(context, location.latitude, location.longitude)
        }
    }
}