package com.example.fitnessapp.data.model

data class UserProfileDto(
    val userId: Int,
    val username: String,
    val email: String,
    val gender: String?,
    val birthDate: String?,
    val heightCm: Int?,
    val weightKg: Int?
)
