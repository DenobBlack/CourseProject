package com.example.fitnessapp.ui.navigation

import androidx.annotation.DrawableRes
import com.example.fitnessapp.R


sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int
) {
    object Home : BottomNavItem(
        "home", "Главная",
        R.drawable.dashboard,
        R.drawable.dashboard_filled
    )

    object Exercises : BottomNavItem(
        "exercises", "Упражнения",
        R.drawable.workouts,
        R.drawable.workouts_filled
    )

    object Meals : BottomNavItem(
        "meals", "Блюда",
        R.drawable.nutrition,
        R.drawable.nutrition_filled
    )

    object Settings : BottomNavItem(
        "settings", "Ещё",
        R.drawable.more,
        R.drawable.more_filled
    )
}