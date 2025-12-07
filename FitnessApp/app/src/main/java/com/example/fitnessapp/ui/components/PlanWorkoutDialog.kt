package com.example.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.data.model.Workout
import java.time.LocalDate

@Composable
fun PlanWorkoutDialog(
    date: LocalDate,
    workouts: List<Workout>,
    selectedWorkoutId: Int?,     // ✅ добавили
    onSelect: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF131316),
        shape = RoundedCornerShape(26.dp),

        title = {
            Text(
                text = "План на $date",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(modifier = Modifier.fillMaxWidth()) {

                // ---- ВЕРХ ----
                Text(
                    "Выберите тренировку:",
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp
                )

                selectedWorkoutId?.let { id ->
                    val workout = workouts.firstOrNull { it.workoutId == id }
                    workout?.let {
                        Text(
                            "Текущий план: ${it.name}",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))

                // ---- СКРОЛЛ ----
                Column(
                    modifier = Modifier
                        .heightIn(max = 280.dp)   // ✅ ограничили высоту
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    workouts.forEach { w ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (w.workoutId == selectedWorkoutId)
                                        Color(0xFF2E7D32)
                                    else Color(0xFF1A1A1D)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )  {
                                    onSelect(w.workoutId)
                                }
                                .padding(vertical = 14.dp, horizontal = 16.dp)
                        ) {
                            Text(
                                w.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // ---- КНОПКА ВСЕГДА ВНИЗУ ----
                if (selectedWorkoutId != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF7A1F1F))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )  {
                                onSelect(null)
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Удалить план", color = Color.White)
                    }
                }
            }
        },

        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Закрыть",
                    color = Color(0xFF3EA0FF),
                    fontSize = 16.sp
                )
            }
        }
    )
}

