package com.example.fitnessapp.data.model

data class WorkoutExerciseUi(
    val name: String,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val previewImageUrl: String? = null,
    val exercise: Exercise? = null
)