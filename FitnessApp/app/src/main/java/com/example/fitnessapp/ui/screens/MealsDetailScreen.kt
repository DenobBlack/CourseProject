package com.example.fitnessapp.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fitnessapp.ui.viewmodel.MealViewModel
import kotlinx.coroutines.delay

@Composable
fun MealDetailScreen(id: Int, viewModel: MealViewModel, userRole: String, navController: NavController) {
    val meal = viewModel.selectedMeal
    val context = LocalContext.current

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.loadMealDetails(id)
    }

    LaunchedEffect(meal) {
        delay(120)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F1A))
    ) {
        meal?.let { m ->
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(450)) + slideInVertically(
                    tween(420),
                    initialOffsetY = { it / 6 }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // IMAGE CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .shadow(12.dp, RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(10.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = m.previewImage,
                                contentDescription = "Блюдо",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color(0xAA000000)),
                                            startY = 120f
                                        )
                                    )
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // NAME
                    Text(
                        text = m.name.uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(Modifier.height(10.dp))

                    // MACRO TAGS
                    Row {
                        PremiumTag("Ккал", m.calories.toString(), Color(0xFF00E676))
                        Spacer(Modifier.width(8.dp))
                        PremiumTag("Белки", "${m.protein} г", Color(0xFF00E5FF))
                        Spacer(Modifier.width(8.dp))
                        PremiumTag("Жиры", "${m.fat} г", Color(0xFFFFB74D))
                        Spacer(Modifier.width(8.dp))
                        PremiumTag("Углеводы", "${m.carbs} г", Color(0xFFFF5252))
                    }

                    Spacer(Modifier.height(22.dp))

                    // DESCRIPTION
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.06f)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text(
                                text = "РЕЦЕПТ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = m.description ?: "Описание отсутствует",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = Color.White.copy(alpha = 0.92f)
                            )
                        }
                    }

                    Spacer(Modifier.height(30.dp))

                    if (userRole == "Administrator") {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SmallActionButton("Изменить") {
                                navController.navigate("mealEdit/${m.mealId}")
                            }
                            SmallActionButton("Удалить") {
                                viewModel.deleteMeal(
                                    m.mealId,
                                    onSuccess = {
                                        Toast.makeText(context, "Блюдо удалено", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TAG
@Composable
fun PremiumTag(title: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SmallActionButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF00E676))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )  { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
