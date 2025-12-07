package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.repository.ApiPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApiSettingsViewModel(
    private val context: Context
) : ViewModel() {

    private val _apiUrl = MutableStateFlow("")
    val apiUrl = _apiUrl.asStateFlow()

    init {
        viewModelScope.launch {
            ApiPreferences.getApiUrl(context).collect {
                _apiUrl.value = it
            }
        }
    }

    fun onUrlChange(newUrl: String) {
        _apiUrl.value = newUrl
    }

    fun saveApiUrl() {
        viewModelScope.launch {
            ApiPreferences.saveApiUrl(context, _apiUrl.value)
        }
    }
}
