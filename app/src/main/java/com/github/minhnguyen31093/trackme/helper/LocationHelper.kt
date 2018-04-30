package com.github.minhnguyen31093.trackme.helper

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.utils.DialogUtils
import com.github.minhnguyen31093.trackme.utils.MapUtils
import com.google.android.gms.location.*


class LocationHelper(private val context: Activity, onLocationListener: OnLocationListener) {

    private val REQUEST_LOCATION_SERVICE = 75
    private var mLastLocation: Location? = null
    private var permissionHelper: PermissionHelper? = null

    private var mFusedLocationClient: FusedLocationProviderClient

    private var isLoadLocationCompleted = false
    private var isForce = true
    private var locationDialog: AlertDialog? = null
    private var onLocationListener: OnLocationListener? = onLocationListener

    private val onPermissionListener = object : PermissionHelper.OnPermissionListener {
        override fun onGranted(currentType: PermissionHelper.PemissionType?) {
            isLoadLocationCompleted = false
            getLastLocation()
        }

        override fun onDenied() {
            if (isForce) {
                context.finish()
            }
        }
    }

    fun onResume() {
        if ((isForce)
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && permissionHelper != null) {
            permissionHelper!!.checkPermission(PermissionHelper.PemissionType.LOCATION)
        } else {
            if (mLastLocation == null || !isLoadLocationCompleted) {
                getLastLocation()
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissionHelper != null) {
            permissionHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun onActivityResult(requestCode: Int?, resultCode: Int?, data: Intent?) {
        if (requestCode == REQUEST_LOCATION_SERVICE || requestCode == PermissionHelper.REQUEST_CODE_ASK_PERMISSIONS) {
            if (locationDialog != null && locationDialog!!.isShowing) {
                locationDialog!!.dismiss()
            }
            if (mLastLocation == null) {
                isLoadLocationCompleted = false
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationClient.lastLocation.addOnCompleteListener(context, { task ->
            if (task.isSuccessful) {
                if (task.result == null) {
                    val locationRequest = LocationRequest()
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    locationRequest.interval = 10000
                    locationRequest.fastestInterval = 1000
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            mFusedLocationClient.removeLocationUpdates(this)
                            if (locationResult != null) {
                                mLastLocation = locationResult.lastLocation
                                if (!isLoadLocationCompleted) {
                                    isLoadLocationCompleted = true
                                    onLocationListener?.onCompleted(mLastLocation)
                                }
                            }
                        }
                    }
                    if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                    }
                } else{
                    mLastLocation = task.result
                    if (!isLoadLocationCompleted) {
                        isLoadLocationCompleted = true
                        onLocationListener?.onCompleted(mLastLocation)
                    }
                }
            } else {
                if (isForce && locationDialog != null && !locationDialog!!.isShowing) {
                    if (MapUtils.isLocationEnabled(context)) {
                        if (mLastLocation == null) {
                            Handler().postDelayed({ getLastLocation() }, 300)
                        }
                    } else {
                        locationDialog!!.show()
                    }
                }
            }
        })
    }

    fun reloadLocation() {
        mLastLocation = null
        isLoadLocationCompleted = false
        getLastLocation()
    }

    interface OnLocationListener {
        fun onCompleted(location: Location?)
    }

    init {
        permissionHelper = PermissionHelper(context)
        permissionHelper!!.setOnPermissionListener(onPermissionListener)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationDialog = DialogUtils.alertDialog(context, R.string.location_service_off_message, R.string.setting, R.string.close, DialogInterface.OnClickListener { dialogInterface, i ->
            when (i) {
                Dialog.BUTTON_POSITIVE -> this@LocationHelper.context.startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_LOCATION_SERVICE)
                DialogInterface.BUTTON_NEGATIVE -> if (isForce) {
                    this@LocationHelper.context.finish()
                }
            }
        })
    }
}