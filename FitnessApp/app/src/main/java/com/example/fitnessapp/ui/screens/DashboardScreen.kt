package com.example.fitnessapp.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.WorkoutCompletionDto
import com.example.fitnessapp.data.model.WorkoutExerciseUi
import com.example.fitnessapp.data.notification.cancelWorkoutNotification
import com.example.fitnessapp.data.notification.scheduleWorkoutNotification
import com.example.fitnessapp.data.repository.WorkoutScheduleStore
import com.example.fitnessapp.ui.components.CircularProgressCard
import com.example.fitnessapp.ui.components.PlanWorkoutDialog
import com.example.fitnessapp.ui.components.WorkoutCalendar
import com.example.fitnessapp.ui.viewmodel.AuthViewModel
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.WaterViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Composable
fun DashboardScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    waterViewModel: WaterViewModel,
    scheduleStore: WorkoutScheduleStore
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val workouts by workoutViewModel.workouts.collectAsState()
    val userId = authViewModel.userId ?: return
    val username = authViewModel.username ?: "Спортсмен"
    val todayCalories by workoutViewModel.todayCalories.collectAsState()
    var showWaterDialog by remember { mutableStateOf(false) }
    val schedule by scheduleStore.scheduleFlow.collectAsState(initial = emptyMap())
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedPlannedWorkoutId by remember { mutableStateOf<Int?>(null) }
    val today = LocalDate.now()
    val plannedTodayWorkoutId = schedule[today]
    LaunchedEffect(userId) {
        workoutViewModel.loadWorkouts(userId)
        workoutViewModel.loadCompletions(userId)
        exerciseViewModel.loadExercises()
        waterViewModel.loadToday(userId)
    }

    val completions by workoutViewModel.completions.collectAsState()
    val completedWorkoutDates = completions.toLocalDates()
    var selectedCompletion by remember { mutableStateOf<WorkoutCompletionDto?>(null) }
    LaunchedEffect(schedule, completions) {
        schedule.forEach { (date, _) ->
            val completed = completions.any { it.completedAt.startsWith(date.toString()) }
            if (date < LocalDate.now() && !completed) {
                scheduleStore.setWorkout(date, null)
            }
        }
    }
    val waterMl by waterViewModel.dailyWater.collectAsState()
    val waterLiters = waterMl / 1000f



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(20.dp)
        ) {
            Column {
                Text("Привет 👋", color = Color.White, fontSize = 20.sp)
                Text(username, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = {
                        if (plannedTodayWorkoutId != null) {
                            navController.navigate("startWorkout/${plannedTodayWorkoutId}")
                        } else {
                            navController.navigate("startWorkout")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Начать тренировку")
                }
            }
        }
        Spacer(Modifier.height(20.dp))
        ProgressRings(
            calories = todayCalories,
            waterLiters = waterLiters,
            onAddWaterClick = { showWaterDialog = true }
        )
        Spacer(Modifier.height(26.dp))
        SectionTitle("Календарь прогресса")


        if (selectedDate != null) {
            PlanWorkoutDialog(
                date = selectedDate!!,
                workouts = workouts,
                selectedWorkoutId = selectedPlannedWorkoutId,  // ✅ передали выбор
                onSelect = { workoutId ->
                    scope.launch {
                        val date = selectedDate ?: return@launch

                        val isCompleted = completions.any { it.completedAt.startsWith(date.toString()) }

                        if (!isCompleted) {
                            selectedPlannedWorkoutId?.let { oldWorkoutId ->
                                schedule.entries.find { it.value == oldWorkoutId }?.key?.let { oldDate ->
                                    if (oldDate != date) {
                                        scheduleStore.setWorkout(oldDate, null)
                                        cancelWorkoutNotification(context, oldDate.hashCode())
                                    }
                                }
                            }

                            scheduleStore.setWorkout(date, workoutId)

                            val workout = workouts.find { it.workoutId == workoutId }
                            workout?.let {
                                scheduleWorkoutNotification(
                                    context = context,
                                    date = date,  // ✔ передаём выбранную дату
                                    workoutName = workout.name,
                                    notificationId = date.hashCode()
                                )
                            }
                        } else {
                            Toast.makeText(context, "Нельзя изменять завершённую тренировку", Toast.LENGTH_SHORT).show()
                        }


                        selectedDate = null
                        selectedPlannedWorkoutId = null
                    }
                },
                onDismiss = {
                    selectedDate = null
                    selectedPlannedWorkoutId = null
                }
            )
        }


        WorkoutCalendar(
            completedDates = completedWorkoutDates,
            plannedDates = schedule.filter { it.key >= LocalDate.now() },
            onCompletedClick = { date ->
                val sameDay = completions.filter { it.completedAt.take(10) == date.toString() }
                val completion = sameDay.maxByOrNull { it.completedAt }

                if (completion != null) {
                    selectedCompletion = completion
                }
            },
            onPlannedClick = { date, workoutId ->
                selectedDate = date
                selectedPlannedWorkoutId = workoutId
            },
            onEmptyClick = { date ->
                if (date.isBefore(LocalDate.now())) {
                    Toast.makeText(context, "Нельзя планировать тренировку в прошлом", Toast.LENGTH_SHORT).show()
                } else {
                    selectedDate = date
                }
            }
        )
        Spacer(Modifier.height(60.dp))
    }
    if (showWaterDialog) {
        AddWaterDialog(
            onAdd = { amount ->
                waterViewModel.addWater(userId, amount)
                showWaterDialog = false
            },
            onDismiss = { showWaterDialog = false }
        )
    }

    selectedCompletion?.let { comp ->
        val uiExercises = workoutViewModel.mergeCompletionExercises(
            comp,
            allExercises = exerciseViewModel.exercises.value
        )

        CompletedWorkoutDialog(
            completion = comp,
            exercises = uiExercises,
            onDismiss = { selectedCompletion = null }
        )
    }
}

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Text(
        text = text,
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Start,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}


@Composable
fun ProgressRings(
    calories: Int,
    waterLiters: Float,
    onAddWaterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        CircularProgressCard(
            title = "Калории",
            currentValue = calories.toFloat(),
            totalValue = 1224f,
            unit = "ккал",
            progressColor = Color(0xFF4CAF50),
            icon = painterResource(R.drawable.ic_fire),
            iconColor = Color.Green
        )

        CircularProgressCard(
            title = "Вода",
            currentValue = waterLiters,
            totalValue = 2.8f,
            unit = "л",
            progressColor = Color(0xFF3EA0FF),
            icon = painterResource(R.drawable.ic_water),
            onPlusClicked = onAddWaterClick,
            iconColor = Color(0xFF2196F3)
        )
    }
}



@Composable
fun AddWaterDialog(
    onAdd: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableIntStateOf(250) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(26.dp),
        containerColor = Color(0xFF141416),
        title = {
            Text(
                "Добавить воду",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                // --- ВВОД МЛ ---
                Text(
                    "Количество (мл)",
                    color = Color(0xFF9CA3AF),
                    fontSize = 15.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    WaterCircleButton("–") {
                        amount = (amount - 50).coerceAtLeast(50)
                    }

                    Text(
                        "$amount мл",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    WaterCircleButton("+") {
                        amount = (amount + 50).coerceAtMost(3000)
                    }
                }

                // Быстрые кнопки
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickWaterButton("250 мл") { amount = 250 }
                    QuickWaterButton("500 мл") { amount = 500 }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickWaterButton("750 мл") { amount = 750 }
                    QuickWaterButton("1 л") { amount = 1000 }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(amount); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Добавить", fontSize = 18.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color(0xFF3EA0FF), fontSize = 16.sp)
            }
        }
    )
}
@Composable
fun WaterCircleButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF1F1F22))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QuickWaterButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1A1D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            color = Color(0xFF3EA0FF),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CompletedWorkoutDialog(
    completion: WorkoutCompletionDto,
    exercises: List<WorkoutExerciseUi>,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        containerColor = Color(0xFF0F0F11),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Завершённая тренировка",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    completion.completedAt.replace("T", " "),
                    color = Color(0xFFAAAAAA),
                    fontSize = 14.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                exercises.forEach { ex ->
                    CompletedExerciseItem(ex)
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Закрыть")
            }
        }
    )
}
@Composable
fun CompletedExerciseItem(ex: WorkoutExerciseUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF18181B))
            .padding(14.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = ex.previewImageUrl,
                contentDescription = ex.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    ex.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "Вес: ${ex.weightKg ?: 0f} кг",
                    color = Color(0xFFAAAAAA),
                    fontSize = 15.sp
                )
            }
        }
    }
}

fun YearMonth.formatRussian(): String {
    val months = listOf(
        "ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ",
        "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"
    )
    return "${months[this.monthValue - 1]} ${this.year}"
}
fun List<WorkoutCompletionDto>.toLocalDates(): List<LocalDate> {
    return this.mapNotNull { dto ->
        try {
            LocalDateTime.parse(dto.completedAt).toLocalDate()
        } catch (e: Exception) {
            null
        }
    }
}