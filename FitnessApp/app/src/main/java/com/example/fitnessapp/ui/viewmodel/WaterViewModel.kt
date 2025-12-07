package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.AddWaterRequest
import com.example.fitnessapp.data.network.ApiService
import com.example.fitnessapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class WaterViewModel(private val context: Context) : ViewModel() {
    private val api = RetrofitClient.create(context)
    private val _dailyWater = MutableStateFlow(0)
    val dailyWater: StateFlow<Int> = _dailyWater

    fun loadToday(userId: Int) {
        viewModelScope.launch {
            try {
                val res = api.getTodayWater(userId)
                if (res.isSuccessful) {
                    _dailyWater.value = res.body() ?: 0
                }
            }catch (e:Exception){
                null
            }
        }
    }

    fun addWater(userId: Int, amount: Int) {
        viewModelScope.launch {
            try {
                api.addWater(AddWaterRequest(userId, amount))
                loadToday(userId)
            }catch (e: Exception){
                null
            }
        }
    }
}
