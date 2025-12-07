package com.example.fitnessapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel

@Composable
fun ExerciseEditScreen(
    viewModel: ExerciseViewModel,
    navController: NavController
) {
    val exercise by viewModel.selectedExercise.collectAsState()
    val context = LocalContext.current

    // ✅ Пока данные не пришли → просто показываем "Загрузка"
    if (exercise == null) {
        Column(Modifier.padding(24.dp)) {
            Text("Загрузка упражнения...")
        }
        return
    }

    val safe = exercise!!   // теперь БЕЗОПАСНО

    var name by remember(safe) { mutableStateOf(safe.name) }
    var description by remember(safe) { mutableStateOf(safe.description ?: "") }
    var muscleGroup by remember(safe) { mutableStateOf(safe.muscleGroup ?: "Грудь") }
    var difficulty by remember(safe) { mutableStateOf(safe.difficulty ?: "Новичок") }
    var equipment by remember(safe) { mutableStateOf(safe.equipment ?: "") }

    var previewImageUri by remember { mutableStateOf<Uri?>(null) }
    var tutorialImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "Редактировать упражнение",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))
        ImagePicker(
            label = "Добавить превью",
            selectedImage = previewImageUri,
            onImageSelected = { previewImageUri = it }
        )
        Spacer(Modifier.height(16.dp))

        // 🔹 GIF/ Tutorial
        ImagePicker(
            label = "Добавить GIF / Tutorial",
            selectedImage = tutorialImageUri,
            onImageSelected = { tutorialImageUri = it }
        )
        Spacer(Modifier.height(16.dp))
        InputField("Название", name) { name = it }
        InputField("Описание", description) { description = it }

        MuscleGroupDropdown(
            selected = muscleGroup,
            onSelect = { muscleGroup = it }
        )

        Spacer(Modifier.height(12.dp))

        DifficultyDropdown(
            selected = difficulty,
            onSelect = { difficulty = it }
        )

        InputField("Оборудование", equipment) { equipment = it }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val updated = safe.copy(
                    name = name,
                    description = description,
                    muscleGroup = muscleGroup,
                    difficulty = difficulty,
                    equipment = equipment,
                )

                viewModel.updateExercise(
                    updated,
                    newPreviewUri = previewImageUri,
                    newTutorialUri = tutorialImageUri,
                    onSuccess = {
                        Toast.makeText(context, "Упражнение обновлено", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}
