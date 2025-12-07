package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun SettingsScreen(
    navController: NavController,
    username: String,
    rolename: String
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(10.dp))

        // ---------------------
        //     HEADER
        // ---------------------
        Text(
            "Настройки",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        // ---------------------
        //     Профиль
        // ---------------------
        Text(
            "Профиль",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Аватар
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9D9D9)),
                )

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        username,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка "Изменить данные"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.navigate("editProfile")
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Изменить данные",
                    fontSize = 17.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(20.dp))



        // ---------------------
        //     Тренировки
        // ---------------------
        SettingsSection(title = "Тренировки") {
            SettingsItem("Автовыбор последней тренировки") {
                navController.navigate("autoSelectWorkout")
            }
            SettingsItem("Время отдыха между подходами") {
                navController.navigate("restTimerSettings")
            }
        }

        Spacer(Modifier.height(20.dp))


        // ---------------------
        //     Безопасность
        // ---------------------
        SettingsSection(title = "Безопасность") {
            SettingsItem("Сменить пароль") {
                navController.navigate("changePassword")
            }
            SettingsItem("Выйти") {
                navController.navigate("exitAccount")
            }
            SettingsItem("Удалить аккаунт") {
                navController.navigate("deleteAccount")
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------------------
        //     Отчёты
        // ---------------------
        SettingsSection(title = "Отчёты") {
            SettingsItem("Экспортировать данные в PDF") {
                navController.navigate("exportPdf")
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------------------
        //     О приложении
        // ---------------------
        SettingsSection(title = "О приложении") {
            SettingsItem("Версия: 1.0.0") {if(rolename == "Administrator")navController.navigate("adminPanel")else return@SettingsItem}
            SettingsItem("Лицензии") {
                navController.navigate("licenses")
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}



@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {

        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            content()
        }
    }
}


@Composable
fun SettingsItem(name: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(name, fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}
