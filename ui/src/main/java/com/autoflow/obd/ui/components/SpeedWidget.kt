package com.autoflow.obd.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autoflow.obd.core.model.TelemetryStatus
import com.autoflow.obd.ui.theme.AccentAmber
import com.autoflow.obd.ui.theme.AccentGreen
import com.autoflow.obd.ui.theme.AccentRed

@Composable
fun SpeedWidget(
    speedValue: Double,
    unit: String,
    status: TelemetryStatus,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        when (status) {
            TelemetryStatus.NORMAL -> AccentGreen
            TelemetryStatus.CAUTION -> AccentAmber
            TelemetryStatus.CRITICAL -> AccentRed
        }, label = "speedColor"
    )
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = speedValue.toInt().toString(),
            style = MaterialTheme.typography.displayLarge,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}
