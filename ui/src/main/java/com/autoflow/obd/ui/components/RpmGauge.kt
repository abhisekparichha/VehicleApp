package com.autoflow.obd.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RpmGauge(
    rpm: Double,
    maxRpm: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val startAngle = 180f
        val sweep = 180f
        drawArc(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            startAngle = startAngle,
            sweepAngle = sweep,
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
        val ratio = (rpm / maxRpm).coerceIn(0.0, 1.0)
        drawArc(
            color = MaterialTheme.colorScheme.primary,
            startAngle = startAngle,
            sweepAngle = (sweep * ratio).toFloat(),
            useCenter = false,
            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        )
        val angleRadians = Math.toRadians((startAngle + sweep * ratio).toDouble())
        val radius = size.minDimension / 2 - 16.dp.toPx()
        val center = Offset(size.width / 2, size.height / 2)
        val needleEnd = Offset(
            (center.x + radius * cos(angleRadians)).toFloat(),
            (center.y + radius * sin(angleRadians)).toFloat()
        )
        drawLine(
            color = MaterialTheme.colorScheme.secondary,
            start = center,
            end = needleEnd,
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
