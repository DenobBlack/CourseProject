package com.example.fitnessapp.data.model

data class Exercise(
    val exerciseId: Int,
    val name: String,
    val muscleGroup: String?,
    val description: String?,
    val equipment: String?,
    val difficulty: String?,
    val previewImage: String?,
    val tutorialImage: String?,
)