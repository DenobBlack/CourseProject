package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.User
import com.example.fitnessapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(context: Context) : ViewModel() {
    private val repository = UserRepository(context)

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val response = repository.getUsers()
                if (response.isSuccessful) {
                    _users.postValue(response.body())
                } else {
                    _error.postValue("Ошибка загрузки: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка подключения: ${e.message}")
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                val response = repository.createUser(user)
                if (response.isSuccessful) {
                    loadUsers() // обновляем список
                } else {
                    _error.postValue("Ошибка добавления: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка: ${e.message}")
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteUser(id)
                if (response.isSuccessful) {
                    loadUsers()
                } else {
                    _error.postValue("Ошибка удаления: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка: ${e.message}")
            }
        }
    }
}
