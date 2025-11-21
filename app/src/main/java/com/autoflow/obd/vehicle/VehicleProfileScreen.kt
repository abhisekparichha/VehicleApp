package com.autoflow.obd.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun VehicleProfileRoute(
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleProfileViewModel = hiltViewModel()
) {
    VehicleProfileScreen(onSave = { vin, nick, maker, model, year, fuel, trans ->
        viewModel.saveProfile(vin, nick, maker, model, year, fuel, trans, onNext)
    }, modifier = modifier)
}

@Composable
fun VehicleProfileScreen(
    onSave: (String, String, String, String, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var vin by rememberSaveable { mutableStateOf("MOCKVIN1234567890") }
    var nickname by rememberSaveable { mutableStateOf("Primary Car") }
    var manufacturer by rememberSaveable { mutableStateOf("Maruti") }
    var model by rememberSaveable { mutableStateOf("Baleno") }
    var year by rememberSaveable { mutableStateOf("2022") }
    var fuel by rememberSaveable { mutableStateOf("Petrol") }
    var transmission by rememberSaveable { mutableStateOf("Automatic") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Vehicle Profile")
        OutlinedTextField(vin, { vin = it.uppercase() }, label = { Text("VIN") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(nickname, { nickname = it }, label = { Text("Nickname") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(manufacturer, { manufacturer = it }, label = { Text("Manufacturer") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(model, { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(year, { year = it.filter { ch -> ch.isDigit() } }, label = { Text("Year") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(fuel, { fuel = it }, label = { Text("Fuel Type") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(transmission, { transmission = it }, label = { Text("Transmission") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            onSave(vin, nickname, manufacturer, model, year.toIntOrNull() ?: 2022, fuel, transmission)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Save & Continue")
        }
    }
}
