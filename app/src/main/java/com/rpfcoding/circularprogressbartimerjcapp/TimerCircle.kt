package com.rpfcoding.circularprogressbartimerjcapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimerCircle(
    remainingTime: Long,
    percentage: Float,
    handleColor: Color,
    inactiveBarColor: Color,
    activeBarColor: Color,
    isActive: Boolean,
    isTimerRunning: Boolean,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 5.dp,
    onToggleClick: (isTimerRunning: Boolean) -> Unit
) {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val remainingTimeFormatted = getFormattedTime(remainingTime)

//    LaunchedEffect(remainingTime, isTimerRunning) {
//        if (remainingTime > 0 && isTimerRunning) {
//            delay(100L)
//            remainingTime -= 100L
//            onTimerTick(elapsedTime + 100L)
//        }
//    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .onSizeChanged {
                size = it
            }
    ) {
        Canvas(modifier = modifier) {
            drawArc(
                color = activeBarColor,
                startAngle = -90f,
                sweepAngle = -360f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = inactiveBarColor,
                startAngle = -90f,
                sweepAngle = -360f * percentage,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            val center = Offset(size.width / 2f, size.height / 2f)
            val beta = (-360f * percentage - 90) * (PI / 180f).toFloat()
            val r = size.width / 2f
            val a = cos(beta) * r
            val b = sin(beta) * r
            drawPoints(
                listOf(Offset(center.x + a, center.y + b)),
                pointMode = PointMode.Points,
                color = handleColor,
                strokeWidth = (strokeWidth * 3f).toPx(),
                cap = StrokeCap.Round
            )
        }

        Text(
            text = remainingTimeFormatted,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Button(
            onClick = {
                if (remainingTime <= 0L) {
                    onToggleClick(true)
                } else {
                    onToggleClick(!isTimerRunning)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (!isTimerRunning || remainingTime <= 0L) {
                    Color.Green
                } else Color.Red
            ),
            enabled = isActive
        ) {
            Text(
                text = if (isTimerRunning && remainingTime > 0L) "Pause"
                else "Start"
            )
        }
    }
}