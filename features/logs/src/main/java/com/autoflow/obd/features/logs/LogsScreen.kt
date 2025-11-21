package com.autoflow.obd.features.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsRoute(
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LogsScreen(state = state, onToggleLogging = viewModel::toggleLogging, modifier = modifier)
}

@Composable
fun LogsScreen(
    state: LogsUiState,
    onToggleLogging: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = rememberFormatter()
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onToggleLogging, modifier = Modifier.fillMaxWidth()) {
            Text(text = if (state.isLogging) "Stop Logging" else "Start Logging")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.trips) { trip ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "VIN: ${trip.vehicleVin}")
                        Text(text = "Start: ${formatter.format(Date(trip.startTime))}")
                        trip.endTime?.let { Text(text = "End: ${formatter.format(Date(it))}") }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberFormatter(): SimpleDateFormat = remember {
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
}
