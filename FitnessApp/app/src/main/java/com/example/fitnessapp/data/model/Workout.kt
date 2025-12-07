package com.example.fitnessapp.data.model

data class Workout(
    val workoutId: Int = 0,
    val userId: Int,
    val name: String,
    val durationMin: Int?,
)