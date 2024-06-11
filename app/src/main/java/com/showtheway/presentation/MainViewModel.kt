package com.showtheway.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Init)
    val state: StateFlow<UiState>
        get() = _state

    fun onPositive() {
        viewModelScope.launch {
            _state.emit(UiState.Success)
        }
    }

    fun onNegative() {
        viewModelScope.launch {
            _state.emit(UiState.Message)
        }
    }

    fun onPermissionRequestResult(result: Boolean) {
        viewModelScope.launch {
            if (result) _state.emit(UiState.Success)
            else _state.emit(UiState.Message)
        }
    }
}