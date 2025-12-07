package com.example.fitnessapp.data.model

data class User(
    val userId: Int,
    val username: String,
    val email: String,
    val gender: String? = null,
    val birthDate: String? = null,
    val heightCm: Int? = null,
    val weightKg: Float? = null,
    val createdAt: String? = null
)