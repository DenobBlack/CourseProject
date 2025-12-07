package com.example.fitnessapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fitnessapp.data.model.WorkoutExerciseUi

@Composable
fun ExerciseCardWithBurger(
    item: WorkoutExerciseUi,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onImageClick: (WorkoutExerciseUi?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF1A1A1C))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ───────────────────────
        // БУРГЕР
        // ───────────────────────
        Column(
            modifier = Modifier
                .padding(end = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onMoveUp() }
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFAAAAAA),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onMoveDown() }
                )
            }
        }

        // ───────────────────────
        // КАРТИНКА
        // ───────────────────────
        AsyncImage(
            model = item.previewImageUrl,
            contentDescription = item.name,
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onImageClick(item)
                },
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(14.dp))

        // ───────────────────────
        // ТЕКСТОВАЯ ЧАСТЬ
        // ───────────────────────
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                item.name,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2
            )

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Подходы: ${item.sets ?: "-"}",
                    color = Color(0xFF9C9C9C),
                    fontSize = 14.sp
                )
                Text(
                    "Повторы: ${item.reps ?: "-"}",
                    color = Color(0xFF9C9C9C),
                    fontSize = 14.sp
                )
            }
        }
    }
}
