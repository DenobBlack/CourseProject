package com.example.fitnessapp.data.model

data class WorkoutCompletionDto(
    val competitionId: Int,
    val workoutId: Int,
    val userId: Int? = null,
    val completedAt: String,
    val exercises: List<CompletionExerciseItem>
)
