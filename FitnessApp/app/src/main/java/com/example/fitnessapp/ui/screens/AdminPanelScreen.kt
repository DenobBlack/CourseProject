package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.data.model.Meal
import com.example.fitnessapp.data.model.User
import com.example.fitnessapp.ui.viewmodel.AdminViewModel

@Composable
fun AdminPanelScreen(
    adminViewModel: AdminViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 — пользователи, 1 — блюда
    var showDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<Meal?>(null) }

    val users by adminViewModel.users.collectAsState()
    val meals by adminViewModel.dishes.collectAsState()
    var editingUser by remember { mutableStateOf<User?>(null) }
    var showUserDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
    ) {

        // ---------------- ТАБЫ ----------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TabButton("Пользователи", selected = selectedTab == 0) { selectedTab = 0 }
            TabButton("Блюда", selected = selectedTab == 1) { selectedTab = 1 }
        }

        Spacer(Modifier.height(12.dp))

        // ---------------- СПИСОК ----------------
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                items(users) { user ->
                    UserCard(
                        user,
                        onEdit = {
                            editingUser = it
                            showUserDialog = true
                        },
                        onDelete = { adminViewModel.deleteUser(user.userId) }
                    )
                }
            } else {
                items(meals) { meal ->
                    MealCard(
                        meal,
                        onEdit = {
                            editingMeal = it
                            showDialog = true
                        },
                        onDelete = { adminViewModel.deleteDish(meal.mealId) }
                    )
                }
            }
        }
    }

    // ---------------- ДИАЛОГ БЛЮДА ----------------
    if (showDialog) {
        MealDialog(
            meal = editingMeal,
            onDismiss = { showDialog = false },
            onSave = {
                adminViewModel.saveDish(it)
                showDialog = false
            }
        )
    }
    if (showUserDialog) {
        UserDialog(
            user = editingUser,
            onDismiss = { showUserDialog = false },
            onSave = {
                adminViewModel.updateUser(it)
                showUserDialog = false
            }
        )
    }
}
@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF3A3A3A) else Color(0xFF1E1E1E)
        ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(text, color = Color.White)
    }
}
@Composable
fun MealCard(
    meal: Meal,
    onEdit: (Meal) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1C), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(meal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("${meal.calories} ккал", fontSize = 14.sp, color = Color.Gray)
            Text("Б: ${meal.protein} Ж: ${meal.fat} У: ${meal.carbs}", color = Color(0xFFBBBBBB))
        }

        Spacer(Modifier.width(12.dp))

        Text("✏",
            color = Color.White,
            modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )   { onEdit(meal) })
        Spacer(Modifier.width(12.dp))
        Text("🗑",
            color = Color.Red,
            modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onDelete() })
    }
}

@Composable
fun MealDialog(
    meal: Meal?,
    onDismiss: () -> Unit,
    onSave: (Meal) -> Unit
) {
    var name by remember { mutableStateOf(meal?.name ?: "") }
    var calories by remember { mutableStateOf(meal?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(meal?.protein?.toString() ?: "") }
    var fat by remember { mutableStateOf(meal?.fat?.toString() ?: "") }
    var carbs by remember { mutableStateOf(meal?.carbs?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (meal == null) "Добавить блюдо" else "Редактировать блюдо") },
        confirmButton = {
            Button(onClick = {
                onSave(
                    Meal(
                        mealId = meal?.mealId ?: 0,
                        name = name,
                        calories = calories.toIntOrNull() ?: 0,
                        protein = protein.toFloat(),
                        fat = fat.toFloat(),
                        carbs = carbs.toFloat(),
                        description = meal?.description,
                        previewImage = meal?.previewImage?: ""
                    )
                )
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Отмена") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(value = name, onValueChange = { name = it }, label = { Text("Название") })
                TextField(value = calories, onValueChange = { calories = it }, label = { Text("Ккал") })
                TextField(value = protein, onValueChange = { protein = it }, label = { Text("Белки") })
                TextField(value = fat, onValueChange = { fat = it }, label = { Text("Жиры") })
                TextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Углеводы") })
            }
        }
    )
}
@Composable
fun UserDialog(
    user: User?,
    onDismiss: () -> Unit,
    onSave: (User) -> Unit
) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать пользователя") },
        confirmButton = {
            Button(onClick = {
                onSave(
                    User(
                        userId = user?.userId ?: 0,
                        username = username,
                        email = email,
                    )
                )
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Отмена") }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Имя") }
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        }
    )
}
@Composable
fun UserCard(
    user: User,
    onEdit: (User) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1A1A1C), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(user.username, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(user.email, fontSize = 14.sp, color = Color.Gray)
        }

        Text("✏",
            color = Color.White,
            modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onEdit(user)
                })
        Spacer(Modifier.width(12.dp))
        Text("🗑",
            color = Color.Red,
            modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )  { onDelete() })
    }
}
