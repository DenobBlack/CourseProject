package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel

@Composable
fun RestTimerSettingsScreen(
    navController: NavHostController,
    workoutVm: WorkoutViewModel
) {
    val seconds by workoutVm.restTimerSeconds.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Время отдыха между подходами",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Настройте интервал отдыха (секунды)", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = seconds.toFloat(),
                    onValueChange = { workoutVm.setRestTimer(it.toInt()) },
                    valueRange = 10f..300f,
                    steps = 28
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Текущее значение: $seconds сек", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад")
        }
    }
}

