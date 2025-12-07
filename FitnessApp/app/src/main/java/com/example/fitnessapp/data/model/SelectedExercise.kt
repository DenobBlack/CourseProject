package com.example.fitnessapp.data.model

data class SelectedExercise(
    val exercise: Exercise,
    val sets: Int,
    val reps: Int,
    val weight: Float
)