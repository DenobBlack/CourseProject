package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.model.WorkoutExerciseUi

@Composable
fun ReorderableExercisesList(
    items: List<WorkoutExerciseUi>,
    onImageClick: (WorkoutExerciseUi?) -> Unit
) {
    var list by remember(items) { mutableStateOf(items) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(list.size) { index ->
            val item = list[index]

            ExerciseCardWithBurger(
                item = item,
                onMoveUp = {
                    if (index > 0) {
                        val new = list.toMutableList()
                        new.removeAt(index).also { new.add(index - 1, it) }
                        list = new
                    }
                },
                onMoveDown = {
                    if (index < list.lastIndex) {
                        val new = list.toMutableList()
                        new.removeAt(index).also { new.add(index + 1, it) }
                        list = new
                    }
                },
                onImageClick = onImageClick
            )
        }
    }
}