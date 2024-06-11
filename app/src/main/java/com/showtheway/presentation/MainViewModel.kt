package com.showtheway.presentation

import android.accounts.NetworkErrorException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.showtheway.R
import com.showtheway.data.MapRepository
import com.yandex.mapkit.directions.driving.DrivingRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Init)
    val state: StateFlow<UiState>
        get() = _state

    private val mapRepository = MapRepository()

    fun onRequestPermissionsResult(result: Boolean) {
        viewModelScope.launch {
            if (result) {
                mapRepository.route.collectLatest {
                    it.fold(
                        onSuccess = ::onSuccess,
                        onFailure = ::onFailure
                    )
                }
                mapRepository.updateRoute()
            } else {
                _state.emit(UiState.Message(R.string.no_permissions_message))
            }
        }
    }

    private fun onSuccess(route: DrivingRoute) {
        _state.value = UiState.Success(route)
    }

    private fun onFailure(throwable: Throwable) {
        _state.value = UiState.Message(
            if (throwable is NetworkErrorException) R.string.network_error_message
            else R.string.unknown_error_message
        )
    }
}

