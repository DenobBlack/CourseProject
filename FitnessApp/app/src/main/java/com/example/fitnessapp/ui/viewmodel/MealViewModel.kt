package com.example.fitnessapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.Meal
import com.example.fitnessapp.data.model.UiState
import com.example.fitnessapp.data.network.NoInternetException
import com.example.fitnessapp.data.network.RetrofitClient
import com.example.fitnessapp.data.network.hasInternetConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class MealViewModel(private val context: Context) : ViewModel() {
    private val api = RetrofitClient.create(context)
    val uiState = MutableStateFlow<UiState>(UiState.Loading)
    var meals by mutableStateOf<List<Meal>>(emptyList())
        private set

    var selectedMeal by mutableStateOf<Meal?>(null)

    fun loadMeals() {
        viewModelScope.launch {
            try {
                val response = api.getMeals()
                if (response.isSuccessful) {
                    meals = response.body() ?: emptyList()
                }
            }catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }

    fun loadMealDetails(id: Int) {
        viewModelScope.launch {
            try {
                val response = api.getMealById(id)
                if (response.isSuccessful) {
                    selectedMeal = response.body()
                }
            } catch (e: NoInternetException) {
                uiState.value = UiState.Error("Нет соединения с интернетом")
            } catch (e: Exception) {
                uiState.value = UiState.Error("Ошибка сервера")
            }
        }
    }
    fun deleteMeal(
        id: Int,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = api.deleteMeal(id)

                if (response.isSuccessful) {
                    // обновляем список блюд
                    loadMeals()

                    onSuccess()
                } else {
                    onError("Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Ошибка сети: ${e.message}")
            }
        }
    }
    var searchQuery by mutableStateOf("")
        private set

    val filteredMeals: List<Meal>
        get() = meals.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

    fun updateSearch(text: String) {
        searchQuery = text
    }

    fun updateMeal(
        meal: Meal,
        newPreviewUri: Uri?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val previewUrl = newPreviewUri?.let { uploadMealImage(it) } ?: meal.previewImage

                val updated = meal.copy(
                    previewImage = previewUrl
                )

                val response = api.updateMeal(updated.mealId, updated)

                if (response.isSuccessful) {
                    loadMeals()
                    onSuccess()
                } else {
                    onError("Ошибка при обновлении блюда")
                }

            } catch (e: NoInternetException) {
                onError("Нет соединения с интернетом")
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Ошибка сервера")
            }
        }
    }

    suspend fun uploadMealImage(uri: Uri): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()

        val requestFile = bytes.toRequestBody("image/*".toMediaType())
        val body = MultipartBody.Part.createFormData(
            "file",
            "meal_${System.currentTimeMillis()}.png",
            requestFile
        )

        val response = api.uploadMealImage(body)
        return response.body()?.imageUrl
    }


}
