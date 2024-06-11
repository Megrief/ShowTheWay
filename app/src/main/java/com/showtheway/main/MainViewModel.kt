package com.showtheway.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.showtheway.R
import com.showtheway.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Init)
    val state: StateFlow<UiState>
        get() = _state

    suspend fun onPermissionRequest(permissionGranted: Boolean, context: Context) {
        if (!checkInternetConnection(context)) _state.emit(UiState.Message(R.string.network_error_message))
        else if (permissionGranted) _state.emit(UiState.Success(Unit))
        else _state.emit(UiState.Message(R.string.no_permissions_message))
    }

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

    suspend fun onFragmentResult(@StringRes message: Int) {
        _state.emit(UiState.Message(message))
    }

    suspend fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val activeNetwork = connectivityManager?.activeNetwork
        val networkCapabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } ?: false
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}

