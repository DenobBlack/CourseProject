package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.viewmodel.ApiSettingsViewModel

@Composable
fun LicensesScreen(navController: NavController, viewModel: ApiSettingsViewModel) {
    val apiUrl by viewModel.apiUrl.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.width(48.dp)
            ) {
                Text("<", fontSize = 32.sp)
            }

            Text(
                "Лицензии",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(Modifier.height(18.dp))

        Text(
            "• Material Design 3\n" +
                    "• Retrofit\n" +
                    "• Coil\n" +
                    "• Kotlin Coroutines\n\n" +
                    "Все библиотеки распространяются под лицензией Apache 2.0.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Адрес API", color = MaterialTheme.colorScheme.onBackground)
            OutlinedTextField(
                value = apiUrl,
                onValueChange = { viewModel.onUrlChange(it) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.saveApiUrl() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}
