package com.example.fitnessapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun RestTimePickerDialog(
    initial: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var minutes by remember { mutableIntStateOf(initial / 60) }
    var seconds by remember { mutableIntStateOf(initial % 60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(minutes * 60 + seconds) }) {
                Text("ОК", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.Gray)
            }
        },
        title = {
            Text("Время отдыха", color = Color.White, fontSize = 20.sp)
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimePickerColumn(
                    label = "Мин",
                    value = minutes,
                    range = 0..10,
                    onValueChange = { minutes = it }
                )
                TimePickerColumn(
                    label = "Сек",
                    value = seconds,
                    range = 0..59,
                    onValueChange = { seconds = it }
                )
            }
        },
        containerColor = Color(0xFF1E1E22)
    )
}