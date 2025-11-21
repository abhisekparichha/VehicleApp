package com.autoflow.obd.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autoflow.obd.core.locale.UnitSystem

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.preferences.collectAsState()
    SettingsScreen(
        modifier = modifier,
        prefs = prefs,
        onUnitChange = viewModel::setUnit,
        onNightModeChange = viewModel::toggleNightMode,
        onHudChange = viewModel::toggleHud,
        onEncryptChange = viewModel::toggleEncryption,
        onCloudChange = viewModel::toggleCloud,
        onSendCommand = viewModel::sendRawCommand
    )
}

@Composable
fun SettingsScreen(
    prefs: com.autoflow.obd.core.locale.UnitPreferences,
    onUnitChange: (UnitSystem) -> Unit,
    onNightModeChange: (Boolean) -> Unit,
    onHudChange: (Boolean) -> Unit,
    onEncryptChange: (Boolean) -> Unit,
    onCloudChange: (Boolean) -> Unit,
    onSendCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var rawCommand by rememberSaveable { mutableStateOf("ATI") }
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Units")
        UnitSystem.values().forEach { unit ->
            RowToggle(
                label = unit.name,
                selected = prefs.system == unit,
                onClick = { onUnitChange(unit) }
            )
        }
        ToggleRow(label = "Night mode", checked = prefs.showNightMode, onCheckedChange = onNightModeChange)
        ToggleRow(label = "HUD mirror", checked = prefs.hudMirrored, onCheckedChange = onHudChange)
        ToggleRow(label = "Encrypt logs", checked = prefs.encryptLogs, onCheckedChange = onEncryptChange)
        ToggleRow(label = "Opt-in cloud uploads", checked = prefs.cloudUploadsEnabled, onCheckedChange = onCloudChange)
        Text(text = "Permissions: Bluetooth, Location, Background DataSync. These are explained during onboarding.")
        OutlinedTextField(
            value = rawCommand,
            onValueChange = { rawCommand = it.uppercase() },
            label = { Text("Raw OBD command") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { onSendCommand(rawCommand) }, modifier = Modifier.fillMaxWidth()) {
            Text("Send Command")
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun RowToggle(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        RadioButton(selected = selected, onClick = onClick)
    }
}
