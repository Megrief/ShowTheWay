package com.showtheway.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.showtheway.R
import com.showtheway.UiState
import com.showtheway.util.ConnectionChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel(private val connectionChecker: ConnectionChecker) : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Init)
    val state: StateFlow<UiState>
        get() = _state

    suspend fun onPermissionRequest(permissionGranted: Boolean) {
        when {
            !connectionChecker.isConnected() -> _state.emit(UiState.Message(R.string.network_error_message))
            permissionGranted  ->  _state.emit(UiState.Success(Unit))
            else -> _state.emit(UiState.Message(R.string.no_permissions_message))
        }
    }

    suspend fun onFragmentResult(@StringRes message: Int) {
        _state.emit(UiState.Message(message))
    }

    companion object {
        const val REQUEST_CODE = 101
    }
}

