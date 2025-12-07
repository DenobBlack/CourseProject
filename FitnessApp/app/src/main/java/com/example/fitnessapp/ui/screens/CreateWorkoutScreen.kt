package com.example.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.SelectedExercise
import com.example.fitnessapp.ui.components.AddExerciseDialog
import com.example.fitnessapp.ui.components.TimeInputDialog
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController,
    userId: Int
) {
    val allExercises by exerciseViewModel.exercises.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var selectedExercises by remember { mutableStateOf<List<SelectedExercise>>(emptyList()) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    var editingInitial by remember { mutableStateOf<SelectedExercise?>(null) }

    var selectedCategory by remember { mutableStateOf("Все") }
    var search by remember { mutableStateOf("") }

    var duration by remember { mutableStateOf("00:30:00") }
    var showDurationDialog by remember { mutableStateOf(false) }

    val categories = listOf("Все", "Грудь", "Спина", "Ноги", "Плечи", "Руки", "Пресс")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новая тренировка", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            // ---- Название
            Text("Название тренировки", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Например: Грудь + трицепс") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ---- Категории
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        text = cat,
                        isSelected = cat == selectedCategory,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ---- Выбранные
            if (selectedExercises.isNotEmpty()) {
                Text("Добавленные упражнения", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(6.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedExercises) { sel ->
                        AssistChip(
                            onClick = {
                                editingExercise = sel.exercise
                                editingInitial = sel
                            },
                            label = { Text(sel.exercise.name) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    selectedExercises = selectedExercises - sel
                                }) {
                                    Icon(
                                        painter = painterResource(android.R.drawable.ic_menu_delete),
                                        contentDescription = null
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // ---- Поиск
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Поиск упражнения") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // ---- Фильтр
            val filtered = allExercises.filter { ex ->
                (selectedCategory == "Все" || ex.muscleGroup?.contains(selectedCategory, true) == true)
                        && (search.isBlank() || ex.name.contains(search, ignoreCase = true))
            }

            // ---- Упражнения
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { ex ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            )  {
                                editingExercise = ex
                                editingInitial =
                                    selectedExercises.find { it.exercise.exerciseId == ex.exerciseId }
                            }
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ex.name, Modifier.weight(1f))
                            Text("➕", fontSize = 20.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ---- Длительность
            OutlinedButton(
                onClick = { showDurationDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⏱ Длительность: $duration")
            }

            Spacer(Modifier.height(12.dp))

            // ---- Сохранение
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (selectedExercises.isEmpty()) {
                        Toast.makeText(context, "Добавьте упражнения", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val durationMin = parseTimeToMinutes(duration).coerceAtLeast(5)

                    workoutViewModel.createNewWorkout(
                        userId = userId,
                        name = name.ifBlank { "Новая тренировка" },
                        exercises = selectedExercises,
                        duration = durationMin
                    ) {
                        navController.popBackStack()
                    }
                }
            ) {
                Text("✅ Создать тренировку", fontSize = 18.sp)
            }
        }

        // ---- Диалоги
        if (editingExercise != null) {
            AddExerciseDialog(
                exercise = editingExercise!!,
                initial = editingInitial,
                onCancel = {
                    editingExercise = null
                    editingInitial = null
                },
                onConfirm = { sets, reps, weight ->
                    val ex = editingExercise!!

                    selectedExercises =
                        selectedExercises.filter { it.exercise.exerciseId != ex.exerciseId } +
                                SelectedExercise(ex, sets, reps, weight)

                    editingExercise = null
                    editingInitial = null
                }
            )
        }

        if (showDurationDialog) {
            TimeInputDialog(
                initial = duration,
                onCancel = { showDurationDialog = false },
                onConfirm = {
                    duration = it
                    showDurationDialog = false
                }
            )
        }
    }
}

fun parseTimeToSeconds(time: String): Int {
    val parts = time.split(":")
    if (parts.size != 3) return 0

    val hours = parts[0].toIntOrNull() ?: 0
    val minutes = parts[1].toIntOrNull() ?: 0
    val seconds = parts[2].toIntOrNull() ?: 0

    return hours * 3600 + minutes * 60 + seconds
}

fun parseTimeToMinutes(time: String): Int {
    return parseTimeToSeconds(time) / 60
}