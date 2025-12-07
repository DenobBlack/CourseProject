package com.example.fitnessapp.data.model

data class CompletionExerciseItem(
    val exerciseId: Int,
    val weight: Float
)

data class CreateCompletionRequest(
    val completedAt: String? = null,
    val exercises: List<CompletionExerciseItem>
)