package com.autoflow.obd.features.diagnostics

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DiagnosticsRoute(
    modifier: Modifier = Modifier,
    viewModel: DiagnosticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    DiagnosticsScreen(
        state = state,
        onReadCodes = viewModel::readCodes,
        onClearCodes = viewModel::clearCodes,
        modifier = modifier
    )
}

@Composable
fun DiagnosticsScreen(
    state: DiagnosticsUiState,
    onReadCodes: () -> Unit,
    onClearCodes: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onReadCodes, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Read DTCs")
        }
        Button(onClick = onClearCodes, enabled = state.canClear, modifier = Modifier.fillMaxWidth()) {
            Text(text = if (state.canClear) "Clear DTCs (Parked)" else "Park to Clear")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.dtcList) { code ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text(
                        text = code,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
