package com.github.minhnguyen31093.trackme.utils

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable
import java.lang.ref.WeakReference


class NetworkUtils {
    companion object {
        fun hasConnection(context: Context): Boolean {
            val weakReference = WeakReference(context)
            if (weakReference.get() != null && weakReference.get()!!.getApplicationContext() != null) {
                val connectivityManager = weakReference.get()!!.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetworkInfo
                return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
            }
            return false
        }

        fun isNetworkAvailableObservable(context: Context): Observable<Boolean> {
            return Observable.just(NetworkUtils.isNetworkAvailable(context))
        }

        private fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}