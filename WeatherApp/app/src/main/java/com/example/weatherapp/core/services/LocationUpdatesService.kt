package com.example.weatherapp.core.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import android.telecom.TelecomManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

import android.content.res.Configuration

import androidx.core.app.NotificationCompat
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import android.app.ActivityManager
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE


class LocationUpdatesService : Service() {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mServiceHandler: Handler
    private lateinit var mNotificationManager: NotificationManager

    private val CHANNEL_ID = "channel_01"
    private val mBinder: LocalBinder = LocalBinder()
    private var mChangingConfiguration = false
    private val NOTIFICATION_ID = 12345678

    companion object {
        private val TAG = javaClass.simpleName
        val ACTION_BROADCAST = "location_broadcast"


        fun startService(context: Context) {
            val startIntent = Intent(context, LocationUpdatesService::class.java)
            Log.i(TAG, "Service started")
            context.startService(startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, LocationUpdatesService::class.java)
            context.stopService(stopIntent)
            Log.i(TAG, "Service stopped")
        }
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService() = this@LocationUpdatesService
    }

    override fun onBind(p0: Intent?): IBinder {
        Log.i(TAG, "in onBind()")
        stopForeground(true)
        mChangingConfiguration = false
        return mBinder
    }

    override fun onRebind(intent: Intent?) {
        Log.i(TAG, "in onRebind()")
        stopForeground(true)
        Log.i(TAG, "Foreground service stopped.")
        mChangingConfiguration = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "in onUnbind()")
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service")
            startForeground(NOTIFICATION_ID, getNotification())
        }
        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "in onStartCommand()")
        requestLocationUpdates()
        return START_NOT_STICKY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        mChangingConfiguration = true
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate() {
        Log.i(TAG, "in onCreate()")
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest()
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }

        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        mServiceHandler = Handler(handlerThread.looper)

        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val name: String = applicationContext.resources.getString(R.string.app_name)
        val mChannel =
            NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
        mNotificationManager.createNotificationChannel(mChannel)
    }

    override fun onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null)
    }

    fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")

        // Notify anyone listening for broadcasts about the new location.
        val intent = Intent(ACTION_BROADCAST)
        intent.putExtra(TelecomManager.EXTRA_LOCATION, location)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification())
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    private fun getNotification(): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, FLAG_IMMUTABLE
        )

        builder.setContentTitle("Weather Forecast")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentText("Tap to check the weather forecast")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        return builder.build()
    }

    private fun serviceIsRunningInForeground(context: Context): Boolean {
        val manager = context.getSystemService(
            ACTIVITY_SERVICE
        ) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (javaClass.name == service.service.className) {
                if (service.foreground) {
                    return true
                }
            }
        }
        return false
    }

    private fun requestLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }
}