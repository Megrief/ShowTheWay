package com.showtheway.presentation

sealed interface UiState {
    data object Init : UiState
    data object Success: UiState
    data object Message: UiState
}
