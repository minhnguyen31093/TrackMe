package com.github.minhnguyen31093.trackme.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.github.minhnguyen31093.trackme.model.*
import com.github.minhnguyen31093.trackme.utils.MapUtils
import com.google.android.gms.location.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*


class LocationService : Service() {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    private val UPDATE_INTERVAL: Long = 10000  /* 10 secs */
    private val FASTEST_INTERVAL: Long = 2000 /* 2 sec */

    var points = ArrayList<RecordLocation>()

    @Subscribe
    fun onEvent(recordPauseEvent: RecordPauseEvent) {
        stopLocationUpdates()
    }

    @Subscribe
    fun onEvent(recordResumeEvent: RecordResumeEvent) {
        startLocationUpdates()
    }

    @Subscribe
    fun onEvent(recordRequestListEvent: RecordRequestListEvent) {
        EventBus.getDefault().post(RecordEvent(points))
    }

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        EventBus.getDefault().register(this)
        return START_STICKY
    }

    override fun onCreate() {
        locationRequest = LocationRequest()

        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = UPDATE_INTERVAL
        locationRequest.fastestInterval = FASTEST_INTERVAL

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        startLocationUpdates()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        stopLocationUpdates()
        super.onDestroy()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun onLocationChanged(location: Location) {
        Log.d("Updated Location", location.latitude.toString() + "," + location.longitude.toString())

        if (points.isEmpty()) {
            points.add(RecordLocation(location.latitude, location.longitude, Date().time))
        } else {
            val preLocation = points.last()
            val locationA = Location("A")
            locationA.latitude = preLocation.lat
            locationA.longitude = preLocation.lng
            if (MapUtils.getDistance(locationA, location) > 5) {
                points.add(RecordLocation(location.latitude, location.longitude, Date().time))
                EventBus.getDefault().post(RecordEvent(points))
            }
        }
    }
}