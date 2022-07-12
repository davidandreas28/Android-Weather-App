package com.example.weatherapp.ui.nextdayssummary

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.todayoverview.MainActivityViewModel
import com.example.weatherapp.R
import com.example.weatherapp.core.repositories.asString
import com.example.weatherapp.core.services.LocationUpdatesService
import com.example.weatherapp.MyApplication
import javax.inject.Inject


class IntraDayWeatherActivity : AppCompatActivity() {

    private var mService: LocationUpdatesService? = null
    private var mBound: Boolean = false
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProvider(viewModelStore, viewModelFactory)[MainActivityViewModel::class.java]
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
        observe()
    }

    private fun observe() {
        viewModel.location.observe(this) {
            setupActionBarTitle(it.asString())
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LocationUpdatesService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    fun setupActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_down, R.anim.slide_down_2)
        }
    }


}