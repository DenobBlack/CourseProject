package com.example.fitnessapp.data.model

data class Meal(
    val mealId: Int,
    val name: String,
    val description: String?,
    val calories: Int,
    val carbs: Float,
    val fat: Float,
    val protein: Float,
    val previewImage: String
)