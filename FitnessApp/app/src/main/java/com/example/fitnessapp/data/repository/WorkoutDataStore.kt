package com.example.fitnessapp.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

val Context.workoutScheduleStore by preferencesDataStore("workout_schedule")

class WorkoutScheduleStore(private val context: Context) {

    private val gson = Gson()
    private val KEY = stringPreferencesKey("schedule_json")

    data class ScheduleDTO(
        val data: Map<String, Int> = emptyMap()
    )

    // Читать
    val scheduleFlow: Flow<Map<LocalDate, Int>> =
        context.workoutScheduleStore.data.map { pref ->
            val json = pref[KEY] ?: return@map emptyMap()
            val dto = gson.fromJson(json, ScheduleDTO::class.java)
            dto.data.mapKeys { LocalDate.parse(it.key) }
        }

    // Добавить / изменить тренировку на дату
    suspend fun setWorkout(date: LocalDate, workoutId: Int?) {
        context.workoutScheduleStore.edit { pref ->
            val oldJson = pref[KEY]

            val old = if (oldJson != null)
                gson.fromJson(oldJson, ScheduleDTO::class.java).data.toMutableMap()
            else
                mutableMapOf()
            Log.d("SCHEDULE", "BEFORE: $old")
            if (workoutId == null) {
                old.remove(date.toString())   // ✅ удаление
            } else {
                old[date.toString()] = workoutId
            }
            Log.d("SCHEDULE", "AFTER: $old")
            pref[KEY] = gson.toJson(ScheduleDTO(old))  // ✅ сохраняем
        }
    }
    // Получить тренировку на дату
    fun getForDate(date: LocalDate, map: Map<LocalDate, Int>): Int? {
        return map[date]
    }
}
