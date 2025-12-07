package com.example.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.components.MonthYearPickerDialog
import com.example.fitnessapp.ui.utils.generatePdfReport
import com.example.fitnessapp.ui.viewmodel.AuthViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import java.time.YearMonth

@Composable
fun ExportPdfScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    userId: Int
) {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val context = LocalContext.current
    var showMonthPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.loadProfile(userId)
        workoutViewModel.loadWorkouts(userId)
        workoutViewModel.workouts.value.forEach { workout ->
            workoutViewModel.loadExercises(workout.workoutId)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Экспорт PDF",
            fontSize = 28.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B1B1F), RoundedCornerShape(14.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { showMonthPicker=true }
                .padding(16.dp)
        ) {
            Text(
                selectedMonth.formatRussian(),
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val profile = authViewModel.profile.value
                if (profile == null) {
                    Toast.makeText(context, "Профиль ещё не загружен", Toast.LENGTH_LONG).show()
                    return@Button
                }
                generatePdfReport(
                    user = profile,
                    workoutsVm = workoutViewModel,
                    exerciseVm = exerciseViewModel,
                    month = selectedMonth,
                    context = context
                )

                Toast.makeText(context, "PDF создан!", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Экспортировать PDF", fontSize = 18.sp)
        }
        if (showMonthPicker) {
            MonthYearPickerDialog(
                current = selectedMonth,
                onDismiss = { showMonthPicker = false },
                onConfirm = {
                    showMonthPicker = false
                    selectedMonth = it
                }
            )
        }
    }
}