package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.CompletionExerciseItem
import com.example.fitnessapp.data.model.CreateCompletionRequest
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.SelectedExercise
import com.example.fitnessapp.data.model.UiState
import com.example.fitnessapp.data.model.Workout
import com.example.fitnessapp.data.model.WorkoutCompletionDto
import com.example.fitnessapp.data.model.WorkoutExercise
import com.example.fitnessapp.data.model.WorkoutExerciseUi
import com.example.fitnessapp.data.network.NoInternetException
import com.example.fitnessapp.data.network.RetrofitClient
import com.example.fitnessapp.data.repository.SettingsRepository
import com.example.fitnessapp.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class WorkoutViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitClient.create(context)
    private val workoutRepository = WorkoutRepository(context)
    val settingsRepo = SettingsRepository(context)
    // ───────────────────────────────────────────────────────────────
    //            СПИСОК ТРЕНИРОВОК ПОЛЬЗОВАТЕЛЯ
    // ───────────────────────────────────────────────────────────────
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts

    val uiState = MutableStateFlow<UiState>(UiState.Loading)

    private val pendingOldWeights = mutableListOf<CompletionExerciseItem>()

    /** Вызвать, когда пользователь подтвердил новый вес — сохраняем старый вес в память (но НЕ создаём completion) */
    fun addPendingOldWeight(exerciseId: Int, oldWeight: Float) {
        // заменяем/обновляем запись для exerciseId, чтобы не дублировать один и тот же ex
        val idx = pendingOldWeights.indexOfFirst { it.exerciseId == exerciseId }
        if (idx != -1) pendingOldWeights[idx] = CompletionExerciseItem(exerciseId, oldWeight)
        else pendingOldWeights.add(CompletionExerciseItem(exerciseId, oldWeight))
    }

    /** Очистить накопленные старые веса (после отправки) */
    fun clearPendingOldWeights() {
        pendingOldWeights.clear()
    }
    // упражнения для каждой тренировки (workoutId → list)
    private val _workoutExercises = MutableStateFlow<Map<Int, List<WorkoutExercise>>>(emptyMap())
    val workoutExercises: StateFlow<Map<Int, List<WorkoutExercise>>> = _workoutExercises

    // выбранная тренировка (диалог)
    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout

    private val _completions = MutableStateFlow<List<WorkoutCompletionDto>>(emptyList())
    val completions: StateFlow<List<WorkoutCompletionDto>> = _completions

    private val _todayCalories = MutableStateFlow(0)
    val todayCalories = _todayCalories.asStateFlow()

    // Автовыбор последней тренировки
    val autoSelectWorkout = settingsRepo.autoSelectWorkoutFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Время отдыха между подходами
    val restTimerSeconds = settingsRepo.restTimerSecondsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, 60)
    private val todayDate = LocalDate.now().toString()

    init {
        viewModelScope.launch {
            settingsRepo.caloriesDateFlow.collect { savedDate ->

                if (savedDate != todayDate) {
                    // новый день → сбрасываем
                    settingsRepo.setTodayCalories(0)
                    settingsRepo.setCaloriesDate(todayDate)
                    _todayCalories.value = 0
                } else {
                    // тот же день → подгружаем сохранённое значение
                    settingsRepo.todayCaloriesFlow.collect { cal ->
                        _todayCalories.value = cal
                    }
                }
            }
        }
    }

    // ───────────────────────────────────────────────────────────────
    //                     ВЫБОР ТРЕНИРОВКИ
    // ───────────────────────────────────────────────────────────────
    fun selectWorkout(workout: Workout) {
        _selectedWorkout.value = workout
    }

    fun closeWorkoutDialog() {
        _selectedWorkout.value = null
    }

    fun setAutoSelectWorkout(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setAutoSelectWorkout(enabled)
        }
    }

    fun setRestTimer(seconds: Int) {
        viewModelScope.launch {
            settingsRepo.setRestTimer(seconds)
        }
    }
    fun saveLastWorkoutId(workoutId: Int) {
        viewModelScope.launch {
            settingsRepo.setLastWorkoutId(workoutId)
        }
    }
    val lastWorkoutId = settingsRepo.lastWorkoutId
    // ───────────────────────────────────────────────────────────────
    //                     ЗАГРУЗКА ДАННЫХ
    // ───────────────────────────────────────────────────────────────

    private fun calcExerciseCalories(ex: WorkoutExerciseUi): Int {
        val sets = ex.sets ?: 1
        val reps = ex.reps ?: 0
        val w = ex.weightKg ?: 0f

        val base =
            if (w > 0f)
                (w * 0.1f + 0.8f)    // силовые
            else
                0.5f                // без веса

        return (sets * reps * base).toInt()
    }

    fun calculateWorkoutCalories(exercises: List<WorkoutExerciseUi>): Int {
        return exercises.sumOf { calcExerciseCalories(it) }
    }


    fun loadWorkouts(userId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getUserWorkouts(userId)
                if (response.isSuccessful) {
                    _workouts.value = response.body() ?: emptyList()
                } else {
                    uiState.value = UiState.Error("Ошибка загрузки тренировок")
                }
            } catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun loadExercises(workoutId: Int) {
        viewModelScope.launch {
            try {
                val response = api.getWorkoutExercises(workoutId)
                if (response.isSuccessful) {
                    _workoutExercises.value = _workoutExercises.value.toMutableMap()
                        .apply { put(workoutId, response.body() ?: emptyList()) }
                } else {
                    uiState.value = UiState.Error("Ошибка загрузки упражнений")
                }
            } catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun loadCompletions(userId: Int) {
        viewModelScope.launch {
            try {
                val res = api.getUserWorkoutCompletions(userId)
                if (res.isSuccessful) {
                    _completions.value = res.body() ?: emptyList()
                }
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка загрузки завершений")
            }
        }
    }
    fun addCompletion(
        workoutId: Int,
        userId: Int,
        exercisesUi: List<WorkoutExerciseUi>,
        onFinished: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                // Список упражнений и весов
                val items = exercisesUi.map {
                    CompletionExerciseItem(
                        exerciseId = it.exercise?.exerciseId ?: 0,
                        weight = it.weightKg ?: 0f
                    )
                }

                val request = CreateCompletionRequest(
                    completedAt = null,
                    exercises = items
                )

                val response = api.createWorkoutCompletion(workoutId, request)

                if (response.isSuccessful) {

                    // посчитать локально калории
                    val calories = calculateWorkoutCalories(exercisesUi)

                    val updated = _todayCalories.value + calories
                    _todayCalories.value = updated
                    settingsRepo.setTodayCalories(updated)
                    settingsRepo.setCaloriesDate(LocalDate.now().toString())

                    // загрузить список всех завершений
                    loadCompletions(userId)

                    onFinished?.invoke()
                }

            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сохранения тренировки")
            }
        }
    }
    fun saveProgress(
        workoutId: Int,
        exerciseId: Int,
        oldWeight: Float,
        newWeight: Float,
        onDone: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // сохраняем старый вес в буфер (НЕ создаём completion)
                addPendingOldWeight(exerciseId, oldWeight)

                // обновляем текущий вес в workout_exercises (PATCH)
                api.updateWorkoutWeight(workoutId = workoutId, exerciseId = exerciseId, weight = newWeight)

                // перезагружаем список упражнений, чтобы UI синхронизировался
                loadExercises(workoutId)

                onDone()
            } catch (e: Exception) {
                Log.e("saveProgress", "Error: ${e.message}")
                onDone()
            }
        }
    }
    fun updateActualWeights(workoutId: Int, exercises: List<WorkoutExerciseUi>) {
        viewModelScope.launch {
            exercises.forEach { ex ->
                val id = ex.exercise?.exerciseId ?: return@forEach
                val weight = ex.weightKg ?: 0f

                try {
                    api.updateWorkoutWeight(workoutId, id, weight)
                } catch (_: Exception) {}
            }
        }
    }
    /** Отправляет один completion со всеми накопленными старыми весами (history), затем очищает буфер.
     *  Возвращает true если успешно.
     */
    fun flushPendingHistoryAndClear(workoutId: Int, userId: Int, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            if (pendingOldWeights.isEmpty()) {
                onComplete(false)
                return@launch
            }

            try {
                val req = CreateCompletionRequest(
                    completedAt = null,
                    exercises = pendingOldWeights.toList()
                )
                val resp = api.createWorkoutCompletion(workoutId, req)
                if (resp.isSuccessful) {
                    // обновляем список завершений и очистка
                    loadCompletions(userId)
                    clearPendingOldWeights()
                    onComplete(true)
                } else {
                    Log.e("flushPendingHistory", "api returned ${resp.code()}")
                    onComplete(false)
                }
            } catch (e: Exception) {
                Log.e("flushPendingHistory", "Exception: ${e.message}")
                onComplete(false)
            }
        }
    }
    // ───────────────────────────────────────────────────────────────
    //                ОБЪЕДИНЕНИЕ УПРАЖНЕНИЙ С ДАННЫМИ
    // ───────────────────────────────────────────────────────────────
    fun mergeExercises(
        workoutExercises: List<WorkoutExercise>,
        allExercises: List<Exercise>
    ): List<WorkoutExerciseUi> {
        return workoutExercises.map { we ->
            val exercise = allExercises.find { it.exerciseId == we.exerciseId }
            WorkoutExerciseUi(
                name = exercise?.name ?: "Неизвестное упражнение",
                sets = we.sets,
                reps = we.reps,
                weightKg = we.weightKg,
                previewImageUrl = exercise?.previewImage,
                exercise = exercise
            )
        }
    }
    fun mergeCompletionExercises(
        completion: WorkoutCompletionDto,
        allExercises: List<Exercise>
    ): List<WorkoutExerciseUi> {

        return completion.exercises.map { item ->
            val base = allExercises.find { it.exerciseId == item.exerciseId }

            WorkoutExerciseUi(
                name = base?.name ?: "Неизвестное упражнение",
                sets = null,
                reps = null,
                weightKg = item.weight,
                previewImageUrl = base?.previewImage,
                exercise = base
            )
        }
    }
    fun createNewWorkout(
        userId: Int,
        name: String,
        exercises: List<SelectedExercise>,
        duration: Int?,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val workout = Workout(
                    userId = userId,
                    name = name,
                    durationMin = duration
                )
                val res = api.createWorkout(workout)
                if (!res.isSuccessful) {
                    Log.d("","Ошибка API: ${res.code()} ${res.message()}")
                    return@launch
                }
                val createdId = res.body()!!.workoutId
                exercises.forEach { sel ->
                    api.addExerciseToWorkout(
                        WorkoutExercise(
                            workoutId = createdId,
                            exerciseId = sel.exercise.exerciseId,
                            sets = sel.sets,
                            reps = sel.reps,
                            weightKg = sel.weight
                        )
                    )
                }
                loadWorkouts(userId)
                onSuccess(createdId)
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сохранения тренировки")
            }
        }
    }

    fun renameWorkout(workoutId: Int, newName: String) {
        viewModelScope.launch {
            try {
                api.renameWorkout(workoutId, newName)
                _workouts.value = _workouts.value.map {
                    if (it.workoutId == workoutId) it.copy(name = newName) else it
                }
            } catch (_: Exception) { }
        }
    }
    fun replaceWorkoutExercises(
        workoutId: Int,
        updated: List<SelectedExercise>
    ) {
        viewModelScope.launch {
            try {
                val current = workoutExercises.value[workoutId] ?: emptyList()

                // --- 1. Находим удалённые упражнения ---
                val removed = current.filter { cur ->
                    updated.none { it.exercise.exerciseId == cur.exerciseId }
                }

                // --- 2. Находим добавленные упражнения ---
                val added = updated.filter { upd ->
                    current.none { it.exerciseId == upd.exercise.exerciseId }
                }

                // --- 3. Находим изменённые (sets/reps/weight) ---
                val modified = updated.filter { upd ->
                    val cur = current.find { it.exerciseId == upd.exercise.exerciseId }
                    cur != null && (
                            cur.sets != upd.sets ||
                                    cur.reps != upd.reps ||
                                    cur.weightKg != upd.weight
                            )
                }

                // --- Удаляем только реально удалённые ---
                removed.forEach { ex ->
                    api.removeExerciseFromWorkout(workoutId, ex.exerciseId)
                }

                // --- Добавляем новые ---
                added.forEach { sel ->
                    api.addExerciseToWorkout(
                        WorkoutExercise(
                            workoutId = workoutId,
                            exerciseId = sel.exercise.exerciseId,
                            sets = sel.sets,
                            reps = sel.reps,
                            weightKg = sel.weight
                        )
                    )
                }

                // --- Обновляем существующие, если изменились ---
                modified.forEach { sel ->
                    api.updateWorkoutExercise(
                        workoutId = workoutId,
                        exerciseId = sel.exercise.exerciseId,
                        ex = WorkoutExercise(workoutId = 0, exerciseId = 0, sets = sel.sets,  reps = sel.reps,  weightKg = sel.weight)
                    )
                }

                // --- Перезагружаем ---
                loadExercises(workoutId)

            } catch (_: Exception) {}
        }
    }
}
