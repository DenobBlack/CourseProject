package com.example.fitnessapp.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val AUTO_SELECT_WORKOUT = booleanPreferencesKey("auto_select_workout")
        val REST_TIMER_SECONDS = intPreferencesKey("rest_timer_seconds")
        val LAST_WORKOUT_ID = intPreferencesKey("last_workout_id")
        private val KEY_TODAY_CALORIES = intPreferencesKey("today_calories")
        private val KEY_CALORIES_DATE = stringPreferencesKey("calories_date")
    }

    val autoSelectWorkoutFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[AUTO_SELECT_WORKOUT] ?: false }

    val restTimerSecondsFlow: Flow<Int> = context.dataStore.data
        .map { prefs -> prefs[REST_TIMER_SECONDS] ?: 60 }

    val lastWorkoutId: Flow<Int?> = context.dataStore.data
        .map { prefs -> prefs[LAST_WORKOUT_ID] }
    val todayCaloriesFlow = context.dataStore.data.map { it[KEY_TODAY_CALORIES] ?: 0 }
    val caloriesDateFlow = context.dataStore.data.map { it[KEY_CALORIES_DATE] ?: "" }

    suspend fun setTodayCalories(value: Int) {
        context.dataStore.edit { it[KEY_TODAY_CALORIES] = value }
    }

    suspend fun setCaloriesDate(date: String) {
        context.dataStore.edit { it[KEY_CALORIES_DATE] = date }
    }

    suspend fun setAutoSelectWorkout(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_SELECT_WORKOUT] = enabled
        }
    }

    suspend fun setRestTimer(seconds: Int) {
        context.dataStore.edit { prefs ->
            prefs[REST_TIMER_SECONDS] = seconds
        }
    }
    suspend fun setLastWorkoutId(workoutId: Int) {
        context.dataStore.edit { prefs ->
            prefs[LAST_WORKOUT_ID] = workoutId
        }
    }
}