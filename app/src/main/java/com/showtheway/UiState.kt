package com.showtheway

import androidx.annotation.StringRes

sealed interface UiState {
    data object Init : UiState
    data class Success<T>(val data: T): UiState
    data class Message(@StringRes val message: Int): UiState
}
