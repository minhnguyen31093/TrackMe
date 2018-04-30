package com.github.minhnguyen31093.trackme.activity

import android.content.Intent
import android.location.Location
import android.os.Bundle
import com.github.minhnguyen31093.trackme.helper.LocationHelper


abstract class BaseMapActivity : BaseActivity(), LocationHelper.OnLocationListener {

    var mLastLocation: Location? = null
    private lateinit var locationHelper: LocationHelper

    override fun onCompleted(location: Location?) {
        mLastLocation = location!!
        onLoadLocationCompleted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationHelper = LocationHelper(this, this)
    }

    override fun onResume() {
        super.onResume()
        locationHelper.onResume()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationHelper.onActivityResult(requestCode, resultCode, data)
    }

    protected abstract fun onLoadLocationCompleted()

    fun reloadLocation() {
        locationHelper.reloadLocation()
    }
}