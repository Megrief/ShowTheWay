package com.showtheway.presentation

import androidx.annotation.StringRes
import com.yandex.mapkit.directions.driving.DrivingRoute

sealed interface UiState {
    data object Init : UiState
    data class Success(val route: DrivingRoute): UiState
    data class Message(@StringRes val message: Int): UiState
}
