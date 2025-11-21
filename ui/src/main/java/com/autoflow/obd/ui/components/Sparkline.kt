package com.autoflow.obd.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun Sparkline(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    if (values.isEmpty()) return
    Canvas(modifier = modifier) {
        val minValue = values.minOrNull() ?: 0.0
        val maxValue = values.maxOrNull() ?: 1.0
        val range = max(1e-3, maxValue - minValue)
        val stepX = size.width / max(1, values.size - 1)
        val path = Path()
        values.forEachIndexed { index, value ->
            val normalizedY = 1 - ((value - minValue) / range)
            val point = Offset(stepX * index, size.height * normalizedY.toFloat())
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(
            path = path,
            color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
