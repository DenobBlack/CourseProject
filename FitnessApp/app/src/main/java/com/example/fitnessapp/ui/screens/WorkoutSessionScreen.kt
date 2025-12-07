// WorkoutSessionScreen.kt
package com.example.fitnessapp.ui.screens

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.WorkoutExerciseUi
import com.example.fitnessapp.data.repository.SettingsRepository
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    workoutId: Int,
    userId: Int,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel,
    settingsRepo: SettingsRepository,
    navController: NavController
) {

    val workouts by workoutViewModel.workouts.collectAsState()
    val exercisesMap by workoutViewModel.workoutExercises.collectAsState()
    val allExercises by exerciseViewModel.exercises.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (allExercises.isEmpty()) exerciseViewModel.loadExercises()
        workoutViewModel.loadExercises(workoutId)
    }

    val workout = workouts.firstOrNull { it.workoutId == workoutId } ?: return
    val exercisesUi = remember(exercisesMap, allExercises) {
        workoutViewModel.mergeExercises(exercisesMap[workoutId] ?: emptyList(), allExercises)
    }.toMutableList()
    var showWeightDialog by remember { mutableStateOf(false) }
    var pendingOldWeight by remember { mutableFloatStateOf(0f) }
    var pendingNewWeight by remember { mutableFloatStateOf(0f) }
    var pendingExerciseId by remember { mutableIntStateOf(0) }

    val initialTotalSec = (workout.durationMin ?: 0).coerceAtLeast(0) * 60
    var remainingSec by remember { mutableIntStateOf(initialTotalSec) }
    var running by remember { mutableStateOf(initialTotalSec > 0) }
    var showExtendDialog by remember { mutableStateOf(false) }
    var moveNextAfterRest by remember { mutableStateOf(false) }
    var showRestOverlay by remember { mutableStateOf(false) }
    var restRemaining by remember { mutableIntStateOf(0) }
    val restDefault by settingsRepo.restTimerSecondsFlow.collectAsState(initial = 60)

    val doneSets = remember { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(exercisesUi) {
        doneSets.clear()
        exercisesUi.indices.forEach { doneSets[it] = 0 }
        remainingSec = initialTotalSec
        running = initialTotalSec > 0
    }

    LaunchedEffect(running) {
        while (running && remainingSec > 0) {
            delay(1000)
            remainingSec -= 1
        }
        if (remainingSec <= 0) {
            running = false
            showExtendDialog = true
        }
    }

    var index by remember { mutableIntStateOf(0) }
    val current = exercisesUi.getOrNull(index)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D0D0F)
    ) {
        Column {

            TopAppBar(
                title = {
                    Text(
                        workout.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(id = R.drawable.ic_back),
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D0D0F)
                )
            )

            SessionHeader(
                remainingSec = remainingSec,
                totalSec = initialTotalSec,
                running = running,
                onToggle = { running = !running }
            )

            if (current != null) {
                ExerciseCardEvolve(
                    exercise = current,
                    done = doneSets[index] ?: 0,
                    onDone = {

                        val total = current.sets ?: 1
                        val already = doneSets[index] ?: 0
                        val newDone = already + 1

                        // фиксируем подход
                        if (already < total) {
                            doneSets[index] = newDone
                        }

                        val isLastSet = newDone >= total

                        if (isLastSet) {

                            // -------------------------------------------------
                            //  ВОТ ГЛАВНОЕ: СОХРАНЯЕМ СТАРЫЙ И НОВЫЙ ВЕС
                            // ------------------------------------------------- // здесь можешь ставить ввод пользователя
                            pendingOldWeight = current.weightKg ?: 0f
                            pendingExerciseId = current.exercise?.exerciseId ?: 0
                            showWeightDialog = true
                            return@ExerciseCardEvolve
                        }

                        // обычный подход → отдых
                        showRestOverlay = true
                        restRemaining = restDefault
                        running = false
                    },
                    onUndo = {
                        if ((doneSets[index] ?: 0) > 0)
                            doneSets[index] = (doneSets[index] ?: 0) - 1
                    },
                    onImageClick = {
                        showExerciseSheet = true
                        running = false
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Нет упражнений", color = Color.Gray)
                }
            }
        }

        if (showRestOverlay) {
            RestOverlayEvolve(
                remaining = restRemaining,
                onSkip = {
                    showRestOverlay = false
                    restRemaining = 0
                    running = true
                },
                onAddTen = { restRemaining += 10 },
                onFinish = {
                    showRestOverlay = false

                    if (moveNextAfterRest) {
                        if (index < exercisesUi.lastIndex) index++
                        else finishWorkout(
                            workoutId = workoutId,
                            userId = userId,
                            exercisesUi = exercisesUi,   // ← вот это важно
                            vm = workoutViewModel,
                            navController = navController
                        )
                    }

                    moveNextAfterRest = false
                    restRemaining = 0
                    running = true
                }
            ) {
                LaunchedEffect(restRemaining, showRestOverlay) {
                    while (showRestOverlay && restRemaining > 0) {
                        delay(1000)
                        restRemaining -= 1
                    }
                }
            }
        }
    }
    if (showWeightDialog) {
        EnterWeightDialog(
            oldWeight = pendingOldWeight,
            onConfirm = { typedNewWeight ->

                // сохраняем новый вес
                pendingNewWeight = typedNewWeight

                val idx = exercisesUi.indexOfFirst { it.exercise?.exerciseId == pendingExerciseId }
                if (idx != -1) {
                    exercisesUi[idx] = exercisesUi[idx].copy(weightKg = typedNewWeight)
                }

                // вызываем сохранение
                workoutViewModel.saveProgress(
                    workoutId = workoutId,
                    exerciseId = pendingExerciseId,
                    oldWeight = pendingOldWeight,
                    newWeight = pendingNewWeight
                ) {

                    showWeightDialog = false

                    // переход
                    val isLast = index == exercisesUi.lastIndex
                    if (!isLast) index++
                    else {
                        finishWorkout(
                            workoutId = workoutId,
                            userId = userId,
                            exercisesUi = exercisesUi,
                            vm = workoutViewModel,
                            navController = navController
                        )
                    }
                }
            },
            onDismiss = { showWeightDialog = false }
        )
    }
    if (showExtendDialog) {
        ExtendDialogEvolve(
            onClose = { showExtendDialog = false },
            onExtend = {
                remainingSec += 300
                running = true
                showExtendDialog = false
            }
        )
    }
    if (showExerciseSheet && current != null) {
        ExerciseBottomSheet(
            exercise = current.exercise ?: return,
            onDismiss = { showExerciseSheet = false; running = true }
        )
    }
}

/* ————————————————— EvolveFIT HEADER ————————————————— */

@Composable
private fun SessionHeader(
    remainingSec: Int,
    totalSec: Int,
    running: Boolean,
    onToggle: () -> Unit
) {
    val total = if (totalSec <= 0) 1 else totalSec
    val progress = (remainingSec.toFloat() / total)
    val animated = animateFloatAsState(progress, tween(650))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Осталось",
            color = Color(0xFF7A7A7D),
            fontSize = 15.sp
        )

        Spacer(Modifier.height(3.dp))

        val mins = TimeUnit.SECONDS.toMinutes(remainingSec.toLong()).toInt()
        val secs = remainingSec % 60

        Text(
            "%02d:%02d".format(mins, secs),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(Modifier.height(4.dp))

        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animated.value },
                modifier = Modifier.size(86.dp),
                color = Color(0xFF00E6FF),
                strokeWidth = 6.dp,
            )
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (running) Color(0xFFFF4A4A) else Color(0xFF10B981))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (running) "||" else "▶",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* ————————————————— EXERCISE CARD ————————————————— */

@Composable
private fun ExerciseCardEvolve(
    exercise: WorkoutExerciseUi,
    done: Int,
    onDone: () -> Unit,
    onUndo: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sets = exercise.sets ?: 1
    val progress = (done.toFloat() / sets).coerceIn(0f, 1f)

    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131316)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp, start = 18.dp, end = 18.dp)
        ) {

            AsyncImage(
                model = exercise.previewImageUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )   {
                        onImageClick()
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(14.dp))

            Text(
                exercise.name,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                "${sets}×${exercise.reps ?: 1}, вес ${exercise.weightKg ?: 0} кг",
                color = Color(0xFF9A9A9C)
            )

            Spacer(Modifier.height(14.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1C1C20))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(progress)
                        .height(12.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00E6FF), Color(0xFF00FFA5))
                            )
                        )
                )
            }

            Spacer(Modifier.height(8.dp))
            Text("$done / $sets", color = Color.White, fontSize = 14.sp)

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onUndo,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Назад") }

                Button(
                    onClick = onDone,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Сделал") }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseBottomSheet(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        ExerciseBottomContent(exercise)
    }
}
@Composable
fun ExerciseBottomContent(ex: Exercise) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(GifDecoder.Factory()) }
            .build()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = ex.name.uppercase(),
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(ex.tutorialImage ?: ex.previewImage)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Упражнение",
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ex.muscleGroup?.let {
                PremiumTag(text = it, color = Color(0xFF00E5FF), icon = Icons.Default.FitnessCenter)
            }
            DifficultyTag(ex.difficulty ?: "—")
        }

        Spacer(Modifier.height(16.dp))

        if (!ex.equipment.isNullOrBlank()) {
            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF64B5F6))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = ex.equipment,
                    color = Color.White.copy(alpha = 0.95f),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF82B1FF))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ИНСТРУКЦИЯ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFBEE6FF)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = ex.description ?: "Описание отсутствует",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.White.copy(alpha = 0.95f)
            )
        }

        Spacer(Modifier.height(28.dp))
    }
}


/* ————————————————— REST OVERLAY ————————————————— */

@Composable
private fun RestOverlayEvolve(
    remaining: Int,
    onSkip: () -> Unit,
    onAddTen: () -> Unit,
    onFinish: () -> Unit,
    content: @Composable () -> Unit
) {
    content()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xCC000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF111B26)),
                contentAlignment = Alignment.Center
            ) {
                val mins = remaining / 60
                val secs = remaining % 60

                Text(
                    "%02d:%02d".format(mins, secs),
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Пропустить") }

                Button(
                    onClick = onAddTen,
                    shape = RoundedCornerShape(14.dp)
                ) { Text("+10 сек") }
            }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(remaining) {
        if (remaining <= 0) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE))

            onFinish()
        }
    }
}

/* ————————————————— EXTEND DIALOG ————————————————— */

@Composable
private fun ExtendDialogEvolve(
    onClose: () -> Unit,
    onExtend: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Время вышло") },
        text = { Text("Продлить тренировку на 5 минут?") },
        confirmButton = {
            TextButton(onClick = onExtend) { Text("Продлить") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Ок") }
        }
    )
}

/* ————————————————— UTILS ————————————————— */

private fun finishWorkout(
    workoutId: Int,
    userId: Int,
    exercisesUi: List<WorkoutExerciseUi>,
    vm: WorkoutViewModel,
    navController: NavController
) {
    vm.flushPendingHistoryAndClear(workoutId, userId) { _ ->

        vm.updateActualWeights(workoutId, exercisesUi)

        val calories = vm.calculateWorkoutCalories(exercisesUi)

        vm.viewModelScope.launch {
            val current = vm.todayCalories.value
            val updated = current + calories

            // сохраняем
            vm.settingsRepo.setTodayCalories(updated)
            vm.settingsRepo.setCaloriesDate(LocalDate.now().toString())
        }
        navController.popBackStack()
    }
}

@Composable
fun EnterWeightDialog(
    oldWeight: Float,
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    var newWeightText by remember { mutableStateOf(oldWeight.toString()) }
    var isCleared by remember { mutableStateOf(false) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Укажи новый вес", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {

                Text("Предыдущий вес: ${oldWeight.toInt()} кг", color = Color.Gray)
                Spacer(Modifier.height(10.dp))

                androidx.compose.material3.OutlinedTextField(
                    value = newWeightText,
                    onValueChange = { txt ->
                        if (txt.matches(Regex("^\\d{0,3}(\\.\\d?)?$")))
                            newWeightText = txt
                    },
                    label = { Text("Новый вес (кг)") },
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused && !isCleared) {
                            newWeightText = ""
                            isCleared = true
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = newWeightText.toFloatOrNull() ?: oldWeight
                    onConfirm(value)
                }
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}