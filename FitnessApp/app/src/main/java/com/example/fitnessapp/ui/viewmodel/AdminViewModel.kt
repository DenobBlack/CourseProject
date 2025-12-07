package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.Meal
import com.example.fitnessapp.data.model.User
import com.example.fitnessapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitClient.create(context)

    /** ---------- USERS ---------- */

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val res = api.getUsers()
                if (res.isSuccessful) {
                    _users.value = res.body() ?: emptyList()
                }
            } catch (_: Exception) { }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                api.deleteUser(id)
                _users.value = _users.value.filter { it.userId != id }
            } catch (_: Exception) { }
        }
    }


    /** ---------- MEALS ---------- */

    private val _dishes = MutableStateFlow<List<Meal>>(emptyList())
    val dishes: StateFlow<List<Meal>> = _dishes

    fun loadMeals() {
        viewModelScope.launch {
            try {
                val res = api.getMeals()
                if (res.isSuccessful) {
                    _dishes.value = res.body() ?: emptyList()
                }
            } catch (_: Exception) { }
        }
    }

    fun deleteDish(id: Int) {
        viewModelScope.launch {
            try {
                api.deleteMeal(id)
                _dishes.value = _dishes.value.filter { it.mealId != id }
            } catch (_: Exception) { }
        }
    }
    fun updateUser(user: User) {
        viewModelScope.launch {
            api.updateUser(user.userId, user)
            loadUsers()
        }
    }
    fun saveDish(meal: Meal) {
        viewModelScope.launch {
            try {
                if (meal.mealId == 0) {
                    // создание нового
                    val res = api.createMeal(meal)
                    if (res.isSuccessful) {
                        _dishes.value = _dishes.value + (res.body()!!)
                    }
                } else {
                    // обновление
                    val res = api.updateMeal(meal.mealId, meal)
                    if (res.isSuccessful) {
                        _dishes.value = _dishes.value.map {
                            if (it.mealId == meal.mealId) meal else it
                        }
                    }
                }
            } catch (_: Exception) { }
        }
    }


    /** ---------- INIT ---------- */

    init {
        loadUsers()
        loadMeals()
    }
}