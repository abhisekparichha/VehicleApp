package com.autoflow.obd.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "DriveSafe OBD",
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = "This app uses Bluetooth & Location to connect to your vehicle’s OBD adapter. Your data stays on device unless you opt into cloud sync.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "For safety, clearing codes and actuator tests can only be run when the app detects the vehicle is parked.",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Agree & Continue")
        }
    }
}
