package com.showtheway.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.showtheway.main.MainActivity
import com.showtheway.main.MainViewModel.Companion.REQUEST_CODE

class PermissionsHandler(private val context: Context) {

    val permissionGranted: () -> Boolean = {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        fineLocationPermission == PackageManager.PERMISSION_GRANTED
                && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(): Boolean {
        val activity = context as? MainActivity

        return if (activity != null) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
             true
        } else false
    }
}