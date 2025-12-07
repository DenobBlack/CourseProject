package com.example.fitnessapp.data.model

data class UserRegisterRequest(
    val userId: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val gender: String,
    val birthDate: String,
    val heightCm: Int,
    val weightKg: Int,
    val createdAt: String,
    val roleId: Int = 0,
    val name: String,
    val lastName: String,
    val patronymic: String? = null
)
