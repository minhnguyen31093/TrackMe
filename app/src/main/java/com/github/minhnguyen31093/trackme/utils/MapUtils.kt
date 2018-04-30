package com.github.minhnguyen31093.trackme.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.github.minhnguyen31093.trackme.R
import com.github.minhnguyen31093.trackme.model.RecordLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*


class MapUtils {
    companion object {
        fun getCurrentLocation(context: Context, onLocationListener: OnLocationListener?) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnCompleteListener({ task ->
                if (onLocationListener != null) {
                    if (task.isSuccessful && task.result != null) {
                        onLocationListener.onCompleted(task.result)
                    } else {
                        val location = Location(LocationManager.NETWORK_PROVIDER)
                        onLocationListener.onCompleted(location)
                    }
                }
            })
        }

        fun checkLocationService(activity: Activity, requestCode: Int) {
            if (!isLocationEnabled(activity)) {
                DialogUtils.alert(activity, R.string.location_service_off_message, R.string.setting, R.string.close, DialogInterface.OnClickListener { dialogInterface, i ->
                    when (i) {
                        Dialog.BUTTON_POSITIVE -> activity.startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), requestCode)
                    }
                })
            }
        }

        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
                return !android.text.TextUtils.isEmpty(locationProviders)
            }
        }

        fun getLastKnownLocation(context: Context, locationManager: LocationManager): Location? {
            val providers = locationManager.getProviders(true)
            var location: Location? = null
            for (i in providers.indices.reversed()) {
                if (locationManager.allProviders.contains(providers[i])) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        break
                    }
                    location = locationManager.getLastKnownLocation(providers[i])
                    if (location != null) {
                        break
                    }
                }
            }

            return location
        }

        private fun getDistance(latA: Double, lngA: Double, latB: Double, lngB: Double): Double {
            val locationA = Location("A")
            locationA.latitude = latA
            locationA.longitude = lngA
            val locationB = Location("B")
            locationB.latitude = latB
            locationB.longitude = lngB
            return locationA.distanceTo(locationB).toDouble() //meters
        }

        fun getDistance(locationA: Location, locationB: Location): Double {
            return locationA.distanceTo(locationB).toDouble() //meters
        }

        fun calculateAvgSpeed(recordLocations: List<RecordLocation>): Double {
            var speed = 0.0
            var count = 0.0
            for (i in 1 until recordLocations.size - 1) {
                val distance = getDistance(recordLocations[i - 1].lat, recordLocations[i - 1].lng, recordLocations[i].lat, recordLocations[i].lng) / 1000
                val milliseconds = recordLocations[i].dateTime - recordLocations[i - 1].dateTime
                val hours = milliseconds.toDouble() / (1000 * 60 * 60) % 24
                speed += distance / hours
                count++
            }
            return speed / count
        }

        fun calculateSpeed(recordLocations: List<RecordLocation>): Double {
            return if (recordLocations.size > 1) {
                val i = recordLocations.size - 1
                val distance = getDistance(recordLocations[i - 1].lat, recordLocations[i - 1].lng, recordLocations[i].lat, recordLocations[i].lng) / 1000
                val milliseconds = recordLocations[i].dateTime - recordLocations[i - 1].dateTime
                val hours = milliseconds.toDouble() / (1000 * 60 * 60) % 24
                distance / hours
            } else {
                0.0
            }
        }

        fun calculateDistance(recordLocations: List<RecordLocation>): Double {
            var distance = 0.0
            var count = 0.0
            for (i in 1 until recordLocations.size - 1) {
                distance += getDistance(recordLocations[i - 1].lat, recordLocations[i - 1].lng, recordLocations[i].lat, recordLocations[i].lng) / 1000
                count++
            }
            return distance / count
        }

        fun calculateTime(recordLocations: List<RecordLocation>): Long {
            return if (recordLocations.size > 1) {
                recordLocations[recordLocations.size - 1].dateTime - recordLocations[0].dateTime
            } else {
                0
            }
        }

        fun moveAndZoomMapTo(googleMap: GoogleMap?, latitude: Double?, longitude: Double?, zoomLevel: Int) {
            if (googleMap != null && latitude != null && longitude != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoomLevel.toFloat()))
            }
        }

        fun drawMarker(googleMap: GoogleMap?, location: LatLng?, markerResId: Int) {
            if (googleMap != null && location != null) {
                googleMap.addMarker(MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(markerResId)))
            }
        }

        fun drawPolyLines(googleMap: GoogleMap, recordLocations: ArrayList<RecordLocation>) {
            val points = ArrayList<LatLng>()
            val lineOptions = PolylineOptions()

            // Traversing through all the routes
            for (recordLocation in recordLocations) {
                points.add(LatLng(recordLocation.lat, recordLocation.lng))
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points)
            lineOptions.width(10f)
            lineOptions.color(Color.RED)
            Log.d("onPostExecute", "onPostExecute lineoptions decoded")

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions)
        }

        fun moveToBounds(googleMap: GoogleMap, recordLocations: ArrayList<RecordLocation>) {
            val builder = LatLngBounds.builder()
            for (recordLocation in recordLocations) {
                builder.include(LatLng(recordLocation.lat, recordLocation.lng))
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        }

        private fun getLatLngBoundsWithLocation(recordLocations: ArrayList<RecordLocation>): LatLngBounds {
            val builder = LatLngBounds.builder()
            for (recordLocation in recordLocations) {
                builder.include(LatLng(recordLocation.lat, recordLocation.lng))
            }
            return builder.build()
        }

        fun getLatLngBoundsWithLocation(location: Location, radius: Double): LatLngBounds {
            return getLatLngBoundsWithLocation(location.latitude, location.longitude, radius)
        }

        fun getLatLngBoundsWithLocation(latLng: LatLng, radius: Double): LatLngBounds {
            return getLatLngBoundsWithLocation(latLng.latitude, latLng.longitude, radius)
        }

        private fun getLatLngBoundsWithLocation(lat: Double, lon: Double, radius: Double): LatLngBounds {
            // radius is in km
            val r = 6371.0 // earth radius in km 26.447521
            val latSw = lat - Math.toDegrees(radius / r)
            val lngSw = lon - Math.toDegrees(radius / r / Math.cos(Math.toRadians(lat)))
            val latNe = lat + Math.toDegrees(radius / r)
            val lngNe = lon + Math.toDegrees(radius / r / Math.cos(Math.toRadians(lat)))
            return LatLngBounds(LatLng(latSw, lngSw), LatLng(latNe, lngNe))
        }

        fun round(number: Double): String {
            return String.format("%.2f", number)
        }

        fun numberToString(number: Int): String {
            return if (number < 10) {
                "0" + number.toString()
            } else {
                number.toString()
            }
        }

        private val LN2 = 0.6931471805599453
        private val WORLD_PX_HEIGHT = 256
        private val WORLD_PX_WIDTH = 256
        private val ZOOM_MAX = 21

        private fun getBoundsZoomLevel(bounds: LatLngBounds, mapWidthPx: Int, mapHeightPx: Int): Int {

            val ne = bounds.northeast
            val sw = bounds.southwest

            val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI

            val lngDiff = ne.longitude - sw.longitude
            val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360

            val latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction)
            val lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction)

            val result = Math.min(latZoom.toInt(), lngZoom.toInt())
            return Math.min(result, 1)
        }

        fun latRad(lat: Double): Double {
            val sin = Math.sin(lat * Math.PI / 180)
            val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
            return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
        }

        fun zoom(mapPx: Int, worldPx: Int, fraction: Double): Double {
            return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2)
        }

        fun getStaticMapImage(recordLocations: ArrayList<RecordLocation>): String {
            return getStaticMapImage(recordLocations, 480, 193)
        }

        private fun getStaticMapImage(recordLocations: ArrayList<RecordLocation>, width: Int, height: Int): String {
            var w = width
            var h = height
            if (w > 480) {
                h = 480 * h / w
                w = 480
            }
            val markerStart = "https://www.tryimg.com/u/2018/04/30/ic_location_startaf681a7e97fb99ec.png"
            val markerEnd = "https://www.tryimg.com/u/2018/04/30/ic_location647682f3986d5937.png"
            val bounds = getLatLngBoundsWithLocation(recordLocations)
            var path = ""
            for (recordLocation in recordLocations) {
                path += "|" + recordLocation.lat.toString() + "," + recordLocation.lng.toString()
            }
            return ("https://maps.googleapis.com/maps/api/staticmap?zoom=" + getBoundsZoomLevel(bounds, w, h)
                    + "&size=" + w + "x" + h
                    + "&maptype=roadmap"
                    + "&center=" + bounds.center.latitude + "," + bounds.center.longitude
                    + "&markers=icon:" + markerStart + "|" + recordLocations[0].lat + "," + recordLocations[0].lng
                    + "&markers=icon:" + markerEnd + "|" + recordLocations[recordLocations.size - 1].lat + "," + recordLocations[recordLocations.size - 1].lng
                    + "&path=color:0xff0000ff|weight:3" + path
                    + "&key=AIzaSyCeqs2nhiSTuFc-xk6Z6od2y72coGX1MBs")
        }

        interface OnLocationListener {
            fun onCompleted(location: Location)
        }
    }
}