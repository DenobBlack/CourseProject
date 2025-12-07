package com.example.fitnessapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessapp.ui.utils.Formatter

@Composable
fun CircularProgressCard(
    title: String,
    currentValue: Float,
    totalValue: Float,
    unit: String,
    progressColor: Color,
    icon: Painter,
    iconColor: Color,
    onPlusClicked: (() -> Unit)? = null
) {
    val progress = (currentValue / totalValue)
        .coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A1A))
            .padding(14.dp)
            .width(125.dp),   // ← фиксированная ширина, больше НЕ растёт
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth().align(Alignment.Start)) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.padding(4.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.padding(6.dp))

        CircularRing(
            progress = progress,
            progressColor = progressColor
        )

        Spacer(Modifier.padding(6.dp))
        Row {
        Text(
            "${Formatter.oneDecimalPlaceWithThousandSeparators(currentValue)} / " +
                    "${Formatter.oneDecimalPlaceWithThousandSeparators(totalValue)} $unit",
            color = Color.LightGray,
            fontSize = 13.sp
        )
            Spacer(Modifier.padding(16.dp))
        if (onPlusClicked != null) {
            Box(
                modifier = Modifier
                    .size(27.dp)
                    .clip(CircleShape)
                    .background(progressColor)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onPlusClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "+",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        }
    }
}

@Composable
fun CircularRing(
    progress: Float,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    progressColor: Color,
    backgroundColor: Color = Color(0xFF333333)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "ring"
    )

    Box(contentAlignment = Alignment.Center) {

        Canvas(modifier = Modifier.size(size)) {
            // BACKGROUND ARC
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // PROGRESS ARC
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            "${(animatedProgress * 100).toInt()}%",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
