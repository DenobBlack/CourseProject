package com.example.fitnessapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.AddWaterRequest
import com.example.fitnessapp.data.model.LoginRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WaterViewModelForTest(
    private val api: FakeWaterApi,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _dailyWater = MutableStateFlow(0)
    val dailyWater: StateFlow<Int> = _dailyWater

    val error = MutableStateFlow<String?>(null)

    fun loadToday(userId: Int) {
        viewModelScope.launch(dispatcher) {
            val res = api.getTodayWater(userId)
            _dailyWater.value = res.body() ?: 0
        }
    }

    fun addWater(userId: Int, amount: Int) {
        viewModelScope.launch(dispatcher) {
            val res = api.addWater(AddWaterRequest(userId, amount))
            if (res.isSuccessful) {
                loadToday(userId)
            } else {
                error.value = "Ошибка при добавлении воды"
            }
        }
    }
}