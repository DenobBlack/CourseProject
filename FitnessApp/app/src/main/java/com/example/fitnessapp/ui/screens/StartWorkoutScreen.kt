package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.WorkoutExerciseUi
import com.example.fitnessapp.ui.components.ReorderableExercisesList
import com.example.fitnessapp.ui.components.WorkoutDropdown
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel

@Composable
fun StartWorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    navController: NavController,
    plannedWorkoutId: Int?
){
    val workouts by workoutViewModel.workouts.collectAsState()
    val workoutExercises by workoutViewModel.workoutExercises.collectAsState()
    val exercises by exerciseViewModel.exercises.collectAsState()
    val lastWorkoutId by workoutViewModel.lastWorkoutId.collectAsState(initial = null)
    var selectedWorkoutId by rememberSaveable { mutableStateOf(lastWorkoutId) }
    val selectedWorkout = workouts.firstOrNull { it.workoutId == selectedWorkoutId }
    var showExerciseSheet by remember { mutableStateOf(false) }
    var currentExercise by remember { mutableStateOf<WorkoutExerciseUi?>(null) }
    LaunchedEffect(plannedWorkoutId, workouts) {
        plannedWorkoutId?.let { id ->
            if (workouts.any { it.workoutId == id }) {
                selectedWorkoutId = id
                workoutViewModel.loadExercises(id)
                workoutViewModel.saveLastWorkoutId(id)
            }
        }
    }
    LaunchedEffect(selectedWorkoutId) {
        selectedWorkoutId?.let {
            workoutViewModel.loadExercises(it)
            workoutViewModel.saveLastWorkoutId(it)
        }
    }
    val exercisesMerged =
        selectedWorkout?.let { w ->
            val wEx = workoutExercises[w.workoutId] ?: emptyList()
            workoutViewModel.mergeExercises(wEx, exercises)
        } ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F))
    ) {

        // ───── НЕ скроллимая верхняя часть ─────
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.width(48.dp)
                ) {
                    Text("<", fontSize = 32.sp)
                }
                Text(
                    "Тренировки",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    WorkoutDropdown(
                        workouts = workouts,
                        selected = selectedWorkout,
                        onSelected = {
                            selectedWorkoutId = it.workoutId
                            workoutViewModel.loadExercises(it.workoutId)
                        }
                    )
                }

                Spacer(Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E1E1E))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { navController.navigate("createWorkout") }
                        .padding(14.dp)
                ) {
                    Text("+", color = Color.White, fontSize = 22.sp)
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedWorkout != null) Color(0xFF1E1E1E) else Color(0xFF3A3A3A)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            enabled = selectedWorkout != null
                        ) {
                            selectedWorkout?.let {
                                navController.navigate("editWorkout/${it.workoutId}")
                            }
                        }
                        .padding(14.dp)
                ) {
                    Text(
                        "✏",
                        color = if (selectedWorkout != null) Color.White else Color.Gray,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // ───── Единственный скроллируемый блок ─────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (selectedWorkout == null) {
                Text(
                    "Выберите тренировку",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                ReorderableExercisesList(
                    items = exercisesMerged,
                    onImageClick = { exercise ->
                        currentExercise = exercise
                        showExerciseSheet = true
                    }
                )
            }
        }

        // ───── Кнопка внизу ─────
        Button(
            onClick = {
                selectedWorkout?.let {
                    navController.navigate("workoutSession/${it.workoutId}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Начать тренировку", fontSize = 18.sp)
        }
    }
    if (showExerciseSheet && currentExercise != null) {
        ExerciseBottomSheet(
            exercise = currentExercise!!.exercise ?: return,
            onDismiss = {
                showExerciseSheet = false
            }
        )
    }
}
