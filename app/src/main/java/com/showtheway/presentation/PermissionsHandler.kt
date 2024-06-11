package com.showtheway.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionsHandler {

    val permissionGranted: (Context) -> Boolean = {
        val fineLocationPermission = ActivityCompat.checkSelfPermission(it,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(it,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        fineLocationPermission == PackageManager.PERMISSION_GRANTED
                && coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(context: Context) {
        val activity = context as? MainActivity

        if (activity != null) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
        }
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}