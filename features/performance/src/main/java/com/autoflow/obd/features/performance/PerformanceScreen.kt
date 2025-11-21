package com.autoflow.obd.features.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PerformanceRoute(
    modifier: Modifier = Modifier,
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    PerformanceScreen(
        state = state,
        onStart = viewModel::startZeroToSixty,
        onReset = viewModel::reset,
        modifier = modifier
    )
}

@Composable
fun PerformanceScreen(
    state: PerformanceUiState,
    onStart: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "0-60 Timer", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = state.zeroToSixtyMs?.let { "${it / 1000.0} s" } ?: "--",
            style = MaterialTheme.typography.displayLarge
        )
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Start Run")
        }
        Button(onClick = onReset, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Reset")
        }
        Text(
            text = "Safety: run on closed roads, have a spotter, and obey local laws.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
