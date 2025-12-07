package com.example.fitnessapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.viewmodel.MealViewModel

@Composable
fun MealEditScreen(
    id: Int,
    viewModel: MealViewModel,
    navController: NavController
) {
    val meal = viewModel.selectedMeal
    val context = LocalContext.current

    LaunchedEffect(id) {
        viewModel.loadMealDetails(id)
    }

    // Пока данных нет – лоадер
    if (meal == null) {
        Column(Modifier.padding(24.dp)) {
            Text("Загрузка блюда...")
        }
        return
    }

    // STATE
    var name by remember(meal) { mutableStateOf(meal.name) }
    var description by remember(meal) { mutableStateOf(meal.description ?: "") }

    var calories by remember(meal) { mutableStateOf(meal.calories.toString()) }
    var protein by remember(meal) { mutableStateOf(meal.protein.toString()) }
    var fat by remember(meal) { mutableStateOf(meal.fat.toString()) }
    var carbs by remember(meal) { mutableStateOf(meal.carbs.toString()) }

    var newPreviewUri by remember { mutableStateOf<Uri?>(null) }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text("Редактировать блюдо", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(16.dp))

        // IMAGE PICKER
        ImagePicker(
            label = "Изображение блюда",
            selectedImage = newPreviewUri,
            onImageSelected = { newPreviewUri = it } // сохраняем только новый Uri
        )

        Spacer(Modifier.height(20.dp))

        InputField("Название", name) { name = it }

        InputField("Описание", description) { description = it }

        Spacer(Modifier.height(10.dp))

        // MACROS
        InputField("Калории (ккал)", calories) { calories = it }
        InputField("Белки (г)", protein) { protein = it }
        InputField("Жиры (г)", fat) { fat = it }
        InputField("Углеводы (г)", carbs) { carbs = it }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (name.isBlank() || calories.isBlank()) {
                    Toast.makeText(context, "Заполните обязательные поля", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                val updated = meal.copy(
                    name = name,
                    description = description,
                    calories = calories.toIntOrNull() ?: meal.calories,
                    protein = protein.toFloatOrNull() ?: meal.protein,
                    fat = fat.toFloatOrNull() ?: meal.fat,
                    carbs = carbs.toFloatOrNull() ?: meal.carbs
                )

                viewModel.updateMeal(
                    updated,
                    newPreviewUri = newPreviewUri,
                    onSuccess = {
                        Toast.makeText(context, "Блюдо обновлено", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        ) {
            Text("Сохранить")
        }
    }
}