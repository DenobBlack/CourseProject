package com.example.fitnessapp.data.repository

import android.content.Context
import com.example.fitnessapp.data.model.User
import com.example.fitnessapp.data.network.RetrofitClient

class UserRepository(private val context: Context) {

    private val userApi = RetrofitClient.create(context)

    suspend fun getUsers() = userApi.getUsers()
    suspend fun createUser(user: User) = userApi.createUser(user)
    suspend fun updateUser(id: Int, user: User) = userApi.updateUser(id, user)
    suspend fun deleteUser(id: Int) = userApi.deleteUser(id)
}
