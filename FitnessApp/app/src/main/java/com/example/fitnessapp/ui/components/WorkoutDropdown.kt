package com.example.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.data.model.Workout

@Composable
fun WorkoutDropdown(
    workouts: List<Workout>,
    selected: Workout?,
    onSelected: (Workout) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E1E))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ){ expanded = true }
                .padding(16.dp)
        ) {
            Text(
                selected?.name ?: "Выберите тренировку",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            workouts.forEach { w ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(w.name) },
                    onClick = {
                        onSelected(w)
                        expanded = false
                    }
                )
            }
        }
    }
}