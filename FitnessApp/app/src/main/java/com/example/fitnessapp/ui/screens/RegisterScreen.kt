package com.example.fitnessapp.ui.screens

import android.icu.text.SimpleDateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun RegisterScreen(viewModel: AuthViewModel, navController: NavController) {

    var step by rememberSaveable { mutableIntStateOf(1) }

    // данные пользователя
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var birthDateInput by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var gender by rememberSaveable { mutableStateOf("male") }
    var name by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var patronymic by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }

    var message by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.navigate("login") }
                ) {
                    Icon(Icons.Default.Close, "Закрыть", tint = Color.Red)
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (step == index + 1) Color(0xFF4CAF50)
                                else Color(0xFFBDBDBD),
                                shape = CircleShape
                            )
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "stepAnimation"
                ) { currentStep ->

                    when (currentStep) {
                        1 -> {
                            // почта и пароль
                            StepContent(
                                title = "Введите почту и пароль"
                            ) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Пароль") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        val emailPattern =
                                            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
                                        if (email.isNotBlank() && email.matches(emailPattern.toRegex()) && password.length >= 8) {
                                            message = ""
                                            step = 2
                                        } else {
                                            message =
                                                "Введите корректный email и пароль (мин. 8 символов)"
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Далее")
                                }
                            }
                        }

                        2 -> {
                            // никнейм
                            StepContent(title = "Введите никнейм") {
                                OutlinedTextField(
                                    value = username,
                                    onValueChange = { username = it },
                                    label = { Text("Никнейм") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = { message = ""; step = 1 },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Назад")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            if (username.isNotBlank()) {
                                                message = ""
                                                step = 3
                                            } else {
                                                message = "Введите никнейм"
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Далее")
                                    }
                                }
                            }
                        }

                        3 -> {

                            // дата рождения + пол
                            StepContent(title = "Дата рождения и пол") {
                                OutlinedTextField(
                                    value = birthDateInput,
                                    onValueChange = { newValue ->
                                        val digits = newValue.text.filter { it.isDigit() }

                                        // Формируем dd.MM.yyyy
                                        val formatted = buildString {
                                            for (i in digits.indices) {
                                                append(digits[i])
                                                if ((i == 1 || i == 3) && i != digits.lastIndex) append(
                                                    '.'
                                                )
                                            }
                                        }
                                        val newCursorPosition = formatted.length

                                        birthDateInput = TextFieldValue(
                                            text = formatted.take(10),
                                            selection = TextRange(
                                                newCursorPosition.coerceAtMost(
                                                    formatted.length
                                                )
                                            )
                                        )
                                    },
                                    label = { Text("Дата рождения") },
                                    placeholder = { Text("дд.ММ.гггг") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth()
                                )


                                Spacer(modifier = Modifier.height(12.dp))

                                Row {
                                    RadioButton(
                                        selected = gender == "male",
                                        onClick = { gender = "male" }
                                    )
                                    Text("Мужской")
                                    Spacer(modifier = Modifier.width(16.dp))
                                    RadioButton(
                                        selected = gender == "female",
                                        onClick = { gender = "female" }
                                    )
                                    Text("Женский")
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = { message = ""; step = 2 },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Назад")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            if (birthDateInput.text.length == 10) {
                                                message = ""
                                                step = 4
                                            } else {
                                                message =
                                                    "Введите дату рождения в формате дд.ММ.гггг"
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Далее")
                                    }
                                }
                            }
                        }

                        4 -> {
                            // имя, фамилия, рост, вес
                            StepContent(title = "Личные данные") {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Имя") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = { lastName = it },
                                    label = { Text("Фамилия") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = patronymic,
                                    onValueChange = { patronymic = it },
                                    label = { Text("Отчество (необязательно)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = height,
                                    onValueChange = { height = it },
                                    label = { Text("Рост (см)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = weight,
                                    onValueChange = { weight = it },
                                    label = { Text("Вес (кг)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = { message = ""; step = 3 },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Назад")
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Button(
                                        onClick = {
                                            val apiBirthDate =
                                                convertToApiFormat(birthDateInput.text).toString()
                                            viewModel.register(
                                                email = email,
                                                username = username,
                                                password = password,
                                                gender = gender,
                                                birthDate = apiBirthDate,
                                                height = height.toIntOrNull() ?: 170,
                                                weight = weight.toIntOrNull() ?: 70,
                                                name = name,
                                                lastName = lastName,
                                                patronymic = patronymic.ifEmpty { null }
                                            ) { success, msg ->
                                                message = msg
                                                if (success) navController.navigate("users")
                                            }
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Завершить")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (message.isNotEmpty()) {
                Text(
                    text = message,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StepContent(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}

fun convertToApiFormat(date: String): String? {
    return try {
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsed = inputFormat.parse(date)
        parsed?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        null
    }
}
