package com.example.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import kotlinx.coroutines.delay

@Composable
fun ExerciseDetailScreen(id: Int, viewModel: ExerciseViewModel, userRole: String, navController: NavController) {
    val exercise = viewModel.selectedExercise.collectAsState().value
    val context = LocalContext.current

    // ImageLoader with GIF support
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(GifDecoder.Factory()) }
            .build()
    }

    // trigger loading
    LaunchedEffect(id) {
        viewModel.loadExerciseDetails(id)
    }

    // for entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(exercise) {
        delay(120)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        exercise?.let { ex ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(450)) + slideInVertically(
                    animationSpec = tween(420),
                    initialOffsetY = { it / 6 }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xAA0B1220)),
                                            startY = 120f
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = ex.name.uppercase(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // muscle group tag
                                ex.muscleGroup?.let { group ->
                                    PremiumTag(text = group, color = Color(0xFF00E5FF), icon = Icons.Default.FitnessCenter)
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                // difficulty
                                val level = ex.difficulty ?: "—"
                                DifficultyTag(level)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Equipment row
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF64B5F6))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = ex.equipment ?: "Без оборудования",
                                color = Color.White.copy(alpha = 0.95f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        color = Color.White.copy(alpha = 0.06f)
                    ) {
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
                    }

                    Spacer(modifier = Modifier.height(26.dp))
                    if (userRole == "Administrator" || userRole == "Coach") {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SmallActionButton("Изменить") {
                                // Навигация на экран редактирования
                                navController.navigate("exerciseEdit/${ex.exerciseId}")
                            }
                            SmallActionButton("Удалить") {
                                viewModel.deleteExercise(
                                    ex.exerciseId,
                                    onSuccess = {
                                        Toast.makeText(context, "Упражнение удалено", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack() // вернуться на список упражнений
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumTag(text: String, color: Color, icon: ImageVector) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon.let {
            Icon(it, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DifficultyTag(level: String) {
    val (color, label) = when (level.lowercase()) {
        "beginner", "новичок", "начинающий" -> Color(0xFF4CAF50) to "Новичок"
        "intermediate", "средний", "любитель" -> Color(0xFFFFC107) to "Средний"
        "advanced", "профессионал", "продвинутый" -> Color(0xFFF44336) to "Продвинутый"
        else -> Color.Gray to level.replaceFirstChar { it.uppercase() }
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(8.dp)
                .width(8.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
