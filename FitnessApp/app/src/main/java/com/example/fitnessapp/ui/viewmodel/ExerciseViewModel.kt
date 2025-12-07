package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.UiState
import com.example.fitnessapp.data.network.NoInternetException
import com.example.fitnessapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ExerciseViewModel(private val context: Context) : ViewModel() {

    private val api = RetrofitClient.create(context)

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises = _exercises

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise = _selectedExercise

    val uiState = MutableStateFlow<UiState>(UiState.Loading)

    init {
        loadExercises()
    }

    fun loadExercises() {
        viewModelScope.launch {
            try {
                uiState.value = UiState.Loading
                val response = api.getExercises()
                if (response.isSuccessful) {
                    _exercises.value = response.body() ?: emptyList()
                } else {
                    uiState.value = UiState.Error("Ошибка загрузки списка упражнений")
                }
            } catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun loadExerciseDetails(id: Int) {
        viewModelScope.launch {
            try {
                _selectedExercise.value = null
                val response = api.getExerciseById(id)
                if (response.isSuccessful) {
                    _selectedExercise.value = response.body()
                } else {
                    uiState.value = UiState.Error("Ошибка получения упражнения")
                }
            } catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun createExercise(
        name: String,
        description: String,
        muscleGroup: String,
        difficulty: String,
        equipment: String,
        previewUri: Uri?,
        tutorialUri: Uri?,
    ) {
        try {
        viewModelScope.launch {
            val previewUrl = previewUri?.let { uploadImage(it) }
            val tutorialUrl = tutorialUri?.let { uploadImage(it) }
            val exercise = Exercise(
                exerciseId = 0,
                name = name,
                description = description,
                muscleGroup = muscleGroup,
                difficulty = difficulty,
                equipment = equipment,
                previewImage = previewUrl,
                tutorialImage = tutorialUrl
            )
            val response = api.createExercise(exercise)
            if(response.isSuccessful)
            {
                loadExercises()
            }
        }
        }catch (e: NoInternetException) {
            uiState.value = UiState.Error("Нет соединения с интернетом")
        } catch (e: Exception) {
            uiState.value = UiState.Error("Ошибка сервера")
        }
    }
    fun deleteExercise(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                api.deleteExercise(id)
                onSuccess()
                loadExercises()
            } catch (e: Exception) {
                onError(e.message ?: "Ошибка удаления")
            }
        }
    }

    fun updateExercise(
        exercise: Exercise,
        newPreviewUri: Uri?,
        newTutorialUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val previewUrl = newPreviewUri?.let { uploadImage(it) } ?: exercise.previewImage
                val tutorialUrl = newTutorialUri?.let { uploadImage(it) } ?: exercise.tutorialImage

                val updated = exercise.copy(
                    previewImage = previewUrl,
                    tutorialImage = tutorialUrl
                )

                val response = api.updateExercise(updated.exerciseId, updated)

                if (response.isSuccessful) {
                    loadExercises()
                    onSuccess()
                } else {
                    onError("Ошибка при обновлении упражнения")
                }

            } catch (e: NoInternetException) {
                onError("Нет соединения с интернетом")
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Ошибка сервера")
            }
        }
    }
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery

    private val _selectedMuscle = MutableStateFlow("Все")
    val selectedMuscle = _selectedMuscle

    private val _selectedLevel = MutableStateFlow("Все")
    val selectedLevel = _selectedLevel

    val filteredExercises = combine(
        exercises,
        searchQuery,
        selectedMuscle,
        selectedLevel
    ) { list, query, muscle, level ->

        list.filter { ex ->
            val matchesName = ex.name.contains(query, ignoreCase = true)
            val matchesMuscle = (muscle == "Все" || ex.muscleGroup == muscle)
            val matchesLevel = (level == "Все" || ex.difficulty.equals(level, true))

            matchesName && matchesMuscle && matchesLevel
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun updateSearch(text: String) {
        _searchQuery.value = text
    }

    fun selectMuscle(group: String) {
        _selectedMuscle.value = group
    }

    fun selectLevel(lvl: String) {
        _selectedLevel.value = lvl
    }

    suspend fun uploadImage(uri: Uri): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()

        val requestFile = bytes.toRequestBody("image/*".toMediaType())
        val body = MultipartBody.Part.createFormData(
            "file",
            "upload_${System.currentTimeMillis()}.png",
            requestFile
        )

        val response = api.uploadExerciseImage(body)
        return response.body()?.imageUrl
    }
}
