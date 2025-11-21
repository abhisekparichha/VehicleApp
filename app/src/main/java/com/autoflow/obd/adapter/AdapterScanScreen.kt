package com.autoflow.obd.adapter

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.autoflow.obd.core.connectivity.AdapterSummary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AdapterScanRoute(
    onAdapterSelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdapterScanViewModel = hiltViewModel()
) {
    val permissions = remember {
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                add(Manifest.permission.BLUETOOTH)
                add(Manifest.permission.BLUETOOTH_ADMIN)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    val adapters by viewModel.adapters.collectAsState()
    AdapterScanScreen(adapters = adapters, onAdapterClick = {
        viewModel.connect(it)
        onAdapterSelected()
    }, modifier = modifier)
}

@Composable
fun AdapterScanScreen(
    adapters: List<AdapterSummary>,
    onAdapterClick: (AdapterSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Select Adapter", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(adapters) { adapter ->
                Card(
                    onClick = { onAdapterClick(adapter) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = adapter.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${adapter.kind} • RSSI ${adapter.rssi ?: "--"}", style = MaterialTheme.typography.bodySmall)
                        adapter.protocolHint?.let {
                            Text(text = it, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
