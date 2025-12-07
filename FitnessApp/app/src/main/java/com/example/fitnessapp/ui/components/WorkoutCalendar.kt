package com.example.fitnessapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.ui.screens.formatRussian
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun WorkoutCalendar(
    modifier: Modifier = Modifier,
    completedDates: List<LocalDate>,
    plannedDates: Map<LocalDate, Int>,   // date -> workoutId
    onCompletedClick: (LocalDate) -> Unit,
    onPlannedClick: (LocalDate, Int) -> Unit,
    onEmptyClick: (LocalDate) -> Unit,
    initialYearMonth: YearMonth = YearMonth.now(),
    onMonthChange: (YearMonth) -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(initialYearMonth) }
    var showMonthPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("<", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(12.dp).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { currentMonth = currentMonth.minusMonths(1); onMonthChange(currentMonth) })

            Text(currentMonth.formatRussian(),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { showMonthPicker = true })

            Text(">", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(12.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { currentMonth = currentMonth.plusMonths(1); onMonthChange(currentMonth) })
        }

        if (showMonthPicker) {
            MonthYearPickerDialog(
                current = currentMonth,
                onDismiss = { showMonthPicker = false },
                onConfirm = {
                    showMonthPicker = false
                    currentMonth = it
                    onMonthChange(it)
                }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Grid: получает LocalDate и мы маршрутизируем событие
        CalendarGrid(
            yearMonth = currentMonth,
            completedDates = completedDates,
            planned = plannedDates,
            onDayClick = { date ->
                when {
                    date in completedDates -> onCompletedClick(date)
                    plannedDates.containsKey(date) -> onPlannedClick(date, plannedDates[date]!!)
                    else -> onEmptyClick(date)
                }
            }
        )
    }
}
