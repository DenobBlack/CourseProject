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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeInputDialog(
    initial: String = "00:00:00",
    onCancel: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initParts = initial.split(":").map { it.toIntOrNull() ?: 0 }

    var hours by remember { mutableIntStateOf(initParts[0]) }
    var minutes by remember { mutableIntStateOf(initParts[1]) }
    var seconds by remember { mutableIntStateOf(initParts[2]) }

    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = Color(0xFF1C1C1E),
        title = {
            Text(
                "Длительность",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                // HOURS PICKER
                TimePickerColumn(
                    label = "Часы",
                    range = 0..11,
                    value = hours,
                    onValueChange = { hours = it }
                )

                // MINUTES PICKER
                TimePickerColumn(
                    label = "Мин",
                    range = 0..59,
                    value = minutes,
                    onValueChange = { minutes = it }
                )

                // SECONDS PICKER
                TimePickerColumn(
                    label = "Сек",
                    range = 0..59,
                    value = seconds,
                    onValueChange = { seconds = it }
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    val formatted = "%02d:%02d:%02d".format(hours, minutes, seconds)
                    onConfirm(formatted)
                }
            ) {
                Text(
                    "ОК",
                    color = Color(0xFF2A82F2),
                    fontSize = 18.sp
                )
            }
        },
        dismissButton = {
            Text(
                "Отмена",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ){ onCancel() }
            )
        }
    )
}

@Composable
fun TimePickerColumn(
    label: String,
    range: IntRange,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    LocalDensity.current

    val itemHeight = 40.dp
    val visibleCount = 5   // 2 сверху, 1 центр, 2 снизу
    val pickerHeight = itemHeight * visibleCount

    val items = listOf(-1, -1) + range.toList() + listOf(-1, -1)
    val realOffset = 2 // сдвиг реальных индексов вверх

    val state = rememberLazyListState(initialFirstVisibleItemIndex = value)

    LaunchedEffect(value) {
        state.scrollToItem(value)
    }

    LaunchedEffect(state) {
        snapshotFlow { !state.isScrollInProgress }
            .collect { stopped ->
                if (stopped) {
                    val centerIndex = state.firstVisibleItemIndex + 2 // центр окна
                    val realIndex = centerIndex - realOffset

                    if (realIndex in range) {
                        onValueChange(realIndex)
                    }
                }
            }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(label, color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            state = state,
            modifier = Modifier
                .height(pickerHeight)
                .width(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2A)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { i ->
                val num = items[i]

                if (num == -1) {
                    Spacer(Modifier.height(itemHeight))
                } else {
                    Text(
                        text = num.toString().padStart(2, '0'),
                        color = if (num == value) Color.White else Color.Gray,
                        fontSize = if (num == value) 26.sp else 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onValueChange(num)
                            }
                    )
                }
            }
        }
    }
}

