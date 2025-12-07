package com.example.fitnessapp.data.model

data class WorkoutExercise(
    val workoutId: Int,
    val exerciseId: Int,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val previewImageUrl: String? = null
)


