package com.github.minhnguyen31093.trackme.helper

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.utils.DialogUtils


class PermissionHelper {

    companion object {
        val REQUEST_CODE_ASK_PERMISSIONS = 123
        val IS_FIRST_TIME_REQUEST_PERMISSION_CAMERA = "IS_FIRST_TIME_REQUEST_PERMISSION_CAMERA"
        val IS_FIRST_TIME_REQUEST_PERMISSION_GALLERY = "IS_FIRST_TIME_REQUEST_PERMISSION_GALLERY"
        val IS_FIRST_TIME_REQUEST_PERMISSION_READ_CONTACTS = "IS_FIRST_TIME_REQUEST_PERMISSION_READ_CONTACTS"
        val IS_FIRST_TIME_REQUEST_PERMISSION_LOCATION = "IS_FIRST_TIME_REQUEST_PERMISSION_LOCATION"
        val IS_FIRST_TIME_REQUEST_PERMISSION_CALL_PHONE = "IS_FIRST_TIME_REQUEST_PERMISSION_CALL_PHONE"
    }

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var onPermissionListener: OnPermissionListener? = null
    private var currentype: PemissionType? = null
    private var sharedPreferences: SharedPreferences? = null

    constructor(activity: Activity) {
        this.activity = activity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity())
    }

    private fun getPermissionsByType(type: PemissionType): Permissions {
        val permissions = Permissions()
        val permissionsNeeded = ArrayList<String>()
        val permissionsList = ArrayList<String>()
        when (type) {
            PermissionHelper.PemissionType.CAMERA -> {
                if (!addPermission(permissionsList, Manifest.permission.CAMERA)) {
                    permissionsNeeded.add("Camera")
                }
                if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permissionsNeeded.add("Write External Storage")
                }
                if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsNeeded.add("Read External Storage")
                }
            }
            PermissionHelper.PemissionType.GALLERY -> {
                if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permissionsNeeded.add("Write External Storage")
                }
                if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    permissionsNeeded.add("Read External Storage")
                }
            }
            PermissionHelper.PemissionType.READ_CONTACTS -> if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS)) {
                permissionsNeeded.add("Contacts")
            }
            PermissionHelper.PemissionType.LOCATION -> if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissionsNeeded.add("Location")
            }
            PermissionHelper.PemissionType.CALL_PHONE -> if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE)) {
                permissionsNeeded.add("Phone")
            }
        }
        permissions.permissionsNeeded = permissionsNeeded
        permissions.permissionsList = permissionsList
        return permissions
    }

    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        val activity = getActivity()
        if (activity != null) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
                // Check for Rationale Option
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getPermissionsStringByType(type: PemissionType?): List<String> {
        val permissions = ArrayList<String>()
        if (type != null) {
            when (type) {
                PermissionHelper.PemissionType.CAMERA -> {
                    permissions.add(Manifest.permission.CAMERA)
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                PermissionHelper.PemissionType.GALLERY -> {
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                PermissionHelper.PemissionType.READ_CONTACTS -> permissions.add(Manifest.permission.READ_CONTACTS)
                PermissionHelper.PemissionType.LOCATION -> permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                PermissionHelper.PemissionType.CALL_PHONE -> permissions.add(Manifest.permission.CALL_PHONE)
            }
        }
        return permissions
    }

    private fun getPreNameByType(type: PemissionType): String? {
        return when (type) {
            PermissionHelper.PemissionType.CAMERA -> IS_FIRST_TIME_REQUEST_PERMISSION_CAMERA
            PermissionHelper.PemissionType.GALLERY -> IS_FIRST_TIME_REQUEST_PERMISSION_GALLERY
            PermissionHelper.PemissionType.READ_CONTACTS -> IS_FIRST_TIME_REQUEST_PERMISSION_READ_CONTACTS
            PermissionHelper.PemissionType.LOCATION -> IS_FIRST_TIME_REQUEST_PERMISSION_LOCATION
            PermissionHelper.PemissionType.CALL_PHONE -> IS_FIRST_TIME_REQUEST_PERMISSION_CALL_PHONE
        }
    }

    fun checkPermission(type: PemissionType) {
        currentype = type
        val preName = getPreNameByType(type)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = getPermissionsByType(type)
            if (permissions.permissionsList != null && permissions.permissionsList!!.size > 0) {
                val permissionsList = permissions.permissionsList
                if (permissions.permissionsNeeded != null && permissions.permissionsNeeded!!.size > 0) {
                    val permissionsNeeded = permissions.permissionsNeeded
                    // Need Rationale
                    if (!sharedPreferences!!.getBoolean(preName, false)) {
                        sharedPreferences!!.edit().putBoolean(preName, true).apply()
                        requestPermissions(permissionsList!!.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
                    } else {
                        val message = StringBuilder("You need to grant access to " + permissionsNeeded!![0])
                        val size = permissionsNeeded.size
                        for (i in 1 until size) {
                            message.append(", ").append(permissionsNeeded[i])
                        }
                        showMessageOKCancel(message.toString(), DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    val uri = Uri.fromParts("package", getPackageName(), null)
                                    intent.setData(uri)
                                    startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS)
                                }
                                DialogInterface.BUTTON_NEGATIVE -> if (onPermissionListener != null) {
                                    onPermissionListener!!.onDenied()
                                }
                                else -> if (onPermissionListener != null) {
                                    onPermissionListener!!.onDenied()
                                }
                            }
                        })
                    }
                } else {
                    requestPermissions(permissionsList!!.toTypedArray(), REQUEST_CODE_ASK_PERMISSIONS)
                }
            } else {
                if (onPermissionListener != null) {
                    onPermissionListener!!.onGranted(currentype)
                }
            }
        } else {
            if (onPermissionListener != null) {
                onPermissionListener!!.onGranted(currentype)
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        DialogUtils.alert(getActivity()!!, message, getString(R.string.setting), getString(R.string.close), okListener)
    }

    private fun getActivity(): Activity? {
        if (activity != null) {
            return activity
        } else if (fragment != null) {
            return fragment!!.getActivity()
        }
        return null
    }

    private fun getString(id: Int): String {
        return if (activity != null) {
            activity!!.getString(id)
        } else if (fragment != null) {
            fragment!!.getString(id)
        } else {
            ""
        }
    }

    private fun getPackageName(): String {
        val activity = getActivity()
        return if (activity != null) {
            activity.packageName
        } else {
            ""
        }
    }

    private fun startActivityForResult(intent: Intent, requestCode: Int) {
        if (activity != null) {
            activity!!.startActivityForResult(intent, requestCode)
        } else if (fragment != null) {
            fragment!!.startActivityForResult(intent, requestCode)
        }
    }

    private fun requestPermissions(permissions: Array<String>, requestCode: Int) {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity!!, permissions, requestCode)
        } else if (fragment != null) {
            fragment!!.requestPermissions(permissions, requestCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                val permissionsString = getPermissionsStringByType(currentype)
                val size = permissionsString.size
                val perms = HashMap<String, Int>()
                // Initial
                for (i in 0 until size) {
                    perms.put(permissionsString[i], PackageManager.PERMISSION_GRANTED)
                }

                // Fill with results
                val len = permissions.size
                for (i in 0 until len) {
                    perms.put(permissions[i], grantResults[i])
                }

                for (i in 0 until size) {
                    if (perms.get(permissionsString[i]) != PackageManager.PERMISSION_GRANTED) {
                        // Permission Denied
                        if (onPermissionListener != null) {
                            onPermissionListener!!.onDenied()
                        }
                        return
                    }
                }
                // All Permissions Granted
                if (onPermissionListener != null) {
                    onPermissionListener!!.onGranted(currentype)
                }
            }
        }
    }

    fun setOnPermissionListener(onPermissionListener: OnPermissionListener) {
        this.onPermissionListener = onPermissionListener
    }

    private inner class Permissions {
        var permissionsNeeded: List<String>? = null
        var permissionsList: List<String>? = null
    }

    enum class PemissionType private constructor(value: Int) {
        CAMERA(0), GALLERY(1), READ_CONTACTS(2), LOCATION(3), CALL_PHONE(4);

        val value: Int = 0

        companion object {
            fun fromValue(value: Int): PemissionType {
                for (c in PemissionType.values()) {
                    if (c.value == value) {
                        return c
                    }
                }
                throw IllegalArgumentException("Invalid PemissionType value: $value")
            }
        }
    }

    interface OnPermissionListener {
        fun onGranted(currentType: PemissionType?)

        fun onDenied()
    }
}