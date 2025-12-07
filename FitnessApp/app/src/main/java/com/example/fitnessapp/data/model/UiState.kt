package com.example.fitnessapp.data.model

sealed class UiState {
    object Loading : UiState()
    data class Error(val message: String) : UiState()
    data class Success<T>(val data: T) : UiState()
}