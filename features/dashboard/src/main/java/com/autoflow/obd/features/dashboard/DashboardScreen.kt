package com.autoflow.obd.features.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoflow.obd.ui.components.MetricCard
import com.autoflow.obd.ui.components.RpmGauge
import com.autoflow.obd.ui.components.SpeedWidget

@Composable
fun DashboardRoute(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) { viewModel.requestMockConnection() }
    DashboardScreen(state = state, modifier = modifier)
}

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val isLandscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SpeedWidget(
                speedValue = state.speed?.value ?: 0.0,
                unit = state.unitLabel,
                status = state.speed?.status ?: com.autoflow.obd.core.model.TelemetryStatus.NORMAL,
                modifier = Modifier.weight(1f)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "RPM", style = MaterialTheme.typography.labelSmall)
                androidx.compose.foundation.layout.Box(modifier = Modifier.height(140.dp)) {
                    RpmGauge(rpm = state.rpm?.value ?: 0.0, maxRpm = 8000.0)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(label = "Coolant", value = "90", unit = "°C", modifier = Modifier.weight(1f))
            MetricCard(label = "Battery", value = "13.9", unit = "V", modifier = Modifier.weight(1f))
            MetricCard(label = "Favorite", value = state.rpm?.value?.toInt()?.toString() ?: "0", unit = "rpm", modifier = Modifier.weight(1f))
        }
        Text(
            text = "Polling ${"%.1f".format(state.pollingRateHz)} Hz",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.weight(1f, fill = !isLandscape))
        Button(
            onClick = { },
            enabled = state.isConnected,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = if (state.isConnected) "Connected" else "Connect")
        }
    }
}
