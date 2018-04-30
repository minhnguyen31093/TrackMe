package com.github.minhnguyen31093.trackme.helper

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.ViewGroup
import com.github.minhnguyen31093.trackme.utils.NetworkUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener


class MapHelper {
    private var context: Activity? = null
    private var location: Location? = null
    private var googleMap: GoogleMap? = null

    private var onMapListener: OnMapListener? = null

    constructor(context: Activity, onMapListener: OnMapListener) {
        this.context = context
        this.onMapListener = onMapListener
        checkGoogleApiAvailability()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
        }
    }

    fun checkGoogleApiAvailability() {
        if (NetworkUtils.hasConnection(context!!.baseContext)) {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            // Getting Google Play availability status
            val status = googleApiAvailability.isGooglePlayServicesAvailable(context)

            // Showing status
            if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
                val requestCode = 10
                googleApiAvailability.getErrorDialog(context, status, requestCode).show()

            } else { // Google Play Services are available
                if (onMapListener != null) {
                    onMapListener!!.onGooglePlayServicesAvailable()
                }
            }
        }
    }

    fun onResume() {
        if (!(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            LocationServices.getFusedLocationProviderClient(context!!).requestLocationUpdates(LocationRequest(), locationCallback, null)
        }
    }

    fun onPause() {
        LocationServices.getFusedLocationProviderClient(context!!).removeLocationUpdates(locationCallback)
    }

    fun setUpMap(googleMap: GoogleMap) {
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        if (!(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            googleMap.isMyLocationEnabled = true
        }
        this.googleMap = googleMap
    }

    fun getLocation(): Location? {
        return location
    }

    interface OnMapListener {
        fun onGooglePlayServicesAvailable()
    }
}