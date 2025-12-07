package com.example.fitnessapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.SelectedExercise

@Composable
fun AddExerciseDialog(
    exercise: Exercise,
    initial: SelectedExercise? = null,
    onCancel: () -> Unit,
    onConfirm: (Int, Int, Float) -> Unit
) {
    var sets by remember { mutableIntStateOf(initial?.sets ?: 3) }
    var reps by remember { mutableIntStateOf(initial?.reps ?: 10) }
    var weight by remember { mutableStateOf((initial?.weight ?: 0f).toString()) }

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                exercise.name,
                color = Color.White,
                fontSize = 20.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Text("Подходы", color = Color.Gray)
                Stepper(value = sets, onValueChange = { sets = it })

                Text("Повторы", color = Color.Gray)
                Stepper(value = reps, onValueChange = { reps = it })

                Text("Вес (кг)", color = Color.Gray)
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White)
                )
            }
        },

        confirmButton = {
            Text(
                if (initial == null) "Добавить" else "Сохранить",
                color = Color(0xFF2A82F2),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onConfirm(sets, reps, weight.toFloatOrNull() ?: 0f)
                }
            )
        },

        dismissButton = {
            Text(
                "Отмена",
                color = Color.Gray,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCancel() }
            )
        }
    )
}