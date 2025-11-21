package com.autoflow.obd.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.core.model.TelemetryValue
import com.autoflow.obd.data.preferences.UserPreferencesRepository
import com.autoflow.obd.obd.repository.ObdConnectionManager
import com.autoflow.obd.obd.usecase.DashboardTelemetryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val telemetryUseCase: DashboardTelemetryUseCase,
    private val preferencesRepository: UserPreferencesRepository,
    private val connectionManager: ObdConnectionManager
) : ViewModel() {

    private val mutableState = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = mutableState.asStateFlow()
    private var lastSpeedTimestamp: Long = 0L

    init {
        combine(
            telemetryUseCase.observeSpeed(),
            telemetryUseCase.observeRpm(),
            preferencesRepository.preferences()
        ) { speed, rpm, prefs ->
            val localizedSpeed = if (prefs.system == com.autoflow.obd.core.locale.UnitSystem.IMPERIAL && speed != null) {
                speed.copy(value = speed.value * 0.621371, unit = "mph")
            } else {
                speed
            }
            val hz = localizedSpeed?.let {
                val now = System.currentTimeMillis()
                val calculated = if (lastSpeedTimestamp > 0) {
                    1000.0 / (now - lastSpeedTimestamp).coerceAtLeast(200)
                } else {
                    0.0
                }
                lastSpeedTimestamp = now
                calculated
            } ?: mutableState.value.pollingRateHz
            mutableState.value.copy(
                speed = localizedSpeed,
                rpm = rpm,
                unitLabel = localizedSpeed?.unit ?: speed?.unit ?: "km/h",
                hudMirrored = prefs.hudMirrored,
                isConnected = connectionManager.state().value is com.autoflow.obd.obd.repository.ConnectionState.Connected,
                pollingRateHz = hz
            )
        }.onEach { mutableState.value = it }.launchIn(viewModelScope)
    }

    fun requestMockConnection() {
        connectionManager.connect(com.autoflow.obd.obd.adapter.MockEndpoint())
    }
}

data class DashboardUiState(
    val speed: TelemetryValue? = null,
    val rpm: TelemetryValue? = null,
    val unitLabel: String = "km/h",
    val hudMirrored: Boolean = false,
    val isConnected: Boolean = false,
    val pollingRateHz: Double = 0.0
)
