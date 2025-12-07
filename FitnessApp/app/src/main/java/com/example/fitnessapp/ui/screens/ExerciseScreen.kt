package com.example.fitnessapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.ui.components.SearchBar
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel


@Composable
fun ExerciseScreen(
    navController: NavController,
    viewModel: ExerciseViewModel,
    roleName: String
) {
    val exercises by viewModel.filteredExercises.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val selectedMuscle by viewModel.selectedMuscle.collectAsState()
    val selectedLevel by viewModel.selectedLevel.collectAsState()
    val filtersVisible = remember { mutableStateOf(false) }
    val isTrainer = (roleName != "User" && roleName != "Guest")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---------- TITLE ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Упражнения",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isTrainer) {
                Text(
                    "+",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )  { navController.navigate("exerciseCreate") }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // ---------- SEARCH ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            SearchBar(
                query = query,
                onChange = { viewModel.updateSearch(it) },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            FilterButton(
                isActive = filtersVisible.value,
                onClick = { filtersVisible.value = !filtersVisible.value }
            )
        }

        Spacer(Modifier.height(12.dp))

        // ---------- FILTERS ----------
        AnimatedVisibility(visible = filtersVisible.value) {
            FilterChips(
                selectedMuscle = selectedMuscle,
                selectedLevel = selectedLevel,
                onMuscleSelected = { viewModel.selectMuscle(it) },
                onLevelSelected = { viewModel.selectLevel(it) }
            )
        }
        Spacer(Modifier.height(12.dp))

        // ---------- GRID SCROLLS, NOT ENTIRE PAGE ----------
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),        // главное!
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = { navController.navigate("exerciseDetail/${exercise.exerciseId}") }
                )
            }
        }
    }
}

@Composable
fun FilterButton(isActive: Boolean, onClick: () -> Unit) {

    val background = if (isActive)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    else
        Color(0xFF1E1E1E)

    val icon = if (isActive)
        R.drawable.ic_close
    else
        R.drawable.ic_filter

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = "Фильтры",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .background(Color(0xFF1E1E1E))
            .aspectRatio(0.9f)
            .shadow(8.dp, RoundedCornerShape(20.dp))
    ) {
        // Фон
        AsyncImage(
            model = exercise.previewImage,
            contentDescription = exercise.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Градиент
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                    )
                )
        )

        // Текст и уровень
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = exercise.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LevelTag(level = exercise.difficulty!!)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = exercise.muscleGroup ?: "",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun FilterChips(
    selectedMuscle: String,
    selectedLevel: String,
    onMuscleSelected: (String) -> Unit,
    onLevelSelected: (String) -> Unit
) {
    val muscles = listOf("Все", "Спина", "Грудь", "Ноги", "Плечи", "Бицепс", "Трицепс", "Пресс")
    val levels = listOf("Все", "Новичок", "Средний", "Продвинутый")

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text("Группа мышц", color = Color.LightGray, fontSize = 13.sp)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(muscles) { m ->
                FilterChip(
                    text = m,
                    isSelected = selectedMuscle == m,
                    onClick = { onMuscleSelected(m) }
                )
            }
        }

        Text("Сложность", color = Color.LightGray, fontSize = 13.sp)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(levels) { l ->
                FilterChip(
                    text = l,
                    isSelected = selectedLevel == l,
                    onClick = { onLevelSelected(l) }
                )
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {

    val background = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    else
        Color(0xFF2A2A2A)

    val border = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(24.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )  { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(text, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LevelTag(level: String) {
    val (color, label) = when (level.lowercase()) {
        "beginner", "новичок", "начинающий" -> Color(0xFF4CAF50) to "Новичок"
        "intermediate", "средний", "любитель" -> Color(0xFFFFC107) to "Средний"
        "advanced", "профессионал", "продвинутый" -> Color(0xFFF44336) to "Продвинутый"
        else -> Color.Gray to level.replaceFirstChar { it.uppercase() }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.25f))
            .border(1.dp, color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}