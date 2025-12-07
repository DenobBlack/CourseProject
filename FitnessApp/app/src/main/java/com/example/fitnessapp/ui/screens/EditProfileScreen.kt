package com.example.fitnessapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.data.model.UserProfileDto
import com.example.fitnessapp.ui.viewmodel.AuthViewModel
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun EditProfileScreen(
    userId: Int,
    navController: NavController,
    viewModel: AuthViewModel
) {
    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    val profile by viewModel.profile.collectAsState()

    if (profile == null) {
        Box(
            Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text("Загрузка...", color = Color.White)
        }
        return
    }

    EditProfileContent(profile!!, navController, viewModel)
}

@Composable
fun EditProfileContent(
    p: UserProfileDto,
    navController: NavController,
    viewModel: AuthViewModel
) {

    var weight by remember { mutableFloatStateOf((p.weightKg ?: 60).toFloat()) }
    var height by remember { mutableFloatStateOf((p.heightCm ?: 170).toFloat()) }
    val gender by remember { mutableStateOf(p.gender ?: "male") }
    var birthDate by remember { mutableStateOf(p.birthDate.orEmpty()) }

    var waterGoal by remember { mutableFloatStateOf(2000f) }
    var calorieGoal by remember { mutableFloatStateOf(2200f) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0C))
            .padding(18.dp)
    ) {

        Text("Редактировать профиль",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(22.dp))


        // ---------- ВЕС ----------
        SectionCard(title = "Вес") {
            SliderRow(
                value = weight,
                label = "${weight.toInt()} кг",
                range = 30f..200f,
                step = 1f
            ) { weight = it }
        }

        // ---------- РОСТ ----------
        SectionCard(title = "Рост") {
            SliderRow(
                value = height,
                label = "${height.toInt()} см",
                range = 120f..220f,
                step = 1f
            ) { height = it }
        }

        // ---------- ДАТА РОЖДЕНИЯ ----------
        SectionCard(title = "Дата рождения") {

            BirthDateButton(
                birthDate = birthDate,
                onPick = { birthDate = it }
            )
        }

        // ---------- ВОДА ----------
        SectionCard(title = "Цель по воде") {
            SliderRow(
                label = "${waterGoal.toInt()} мл",
                value = waterGoal,
                range = 1000f..5000f,
                step = 250f
            ) { waterGoal = it }
        }

        // ---------- КАЛОРИИ ----------
        SectionCard(title = "Цель по калориям") {
            SliderRow(
                label = "${calorieGoal.toInt()} ккал",
                value = calorieGoal,
                range = 1200f..5000f,
                step = 50f
            ) { calorieGoal = it }
        }

        Spacer(Modifier.height(28.dp))

        Button(
            onClick = {
                viewModel.updateProfile(
                    UserProfileDto(
                        userId = p.userId,
                        username = p.username,
                        email = p.email,
                        gender = gender,
                        birthDate = birthDate,
                        heightCm = height.toInt(),
                        weightKg = weight.toInt()
                    )
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3EA0FF)
            )
        ) {
            Text("Сохранить", color = Color.White, fontSize = 18.sp)
        }
    }
}

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDateButton(birthDate: String, onPick: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val locale = Locale("ru")
    Locale.setDefault(locale)
    val context = LocalContext.current
    val config = context.resources.configuration
    config.setLocale(locale)
    if (open) {
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    open = false

                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }

                        val year = cal.get(Calendar.YEAR)
                        val month = cal.get(Calendar.MONTH) + 1
                        val day = cal.get(Calendar.DAY_OF_MONTH)

                        onPick("$year-$month-$day")
                    }

                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1A1D))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )  { open = true }
            .padding(16.dp)
    ) {
        Text(
            birthDate.ifEmpty { "Выбрать" },
            color = Color(0xFF3EA0FF),
            fontSize = 16.sp
        )
    }
}


@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF111114))
            .padding(16.dp)
    ) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))
        content()
    }

    Spacer(Modifier.height(14.dp))
}


@Composable
fun SliderRow(
    value: Float,
    label: String,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
    onChange: (Float) -> Unit
) {
    Column {
        Text(label, color = Color(0xFF3EA0FF), fontWeight = FontWeight.Bold)
        Slider(
            value = value,
            onValueChange = { onChange((it / step).roundToInt() * step) },
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF3EA0FF),
                activeTrackColor = Color(0xFF3EA0FF)
            )
        )
    }
}
