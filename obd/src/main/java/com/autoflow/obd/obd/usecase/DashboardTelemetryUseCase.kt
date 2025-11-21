package com.autoflow.obd.obd.usecase

import com.autoflow.obd.core.model.TelemetryValue
import com.autoflow.obd.obd.repository.ObdConnectionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DashboardTelemetryUseCase @Inject constructor(
    private val connectionManager: ObdConnectionManager
) {
    fun observeSpeed(): Flow<TelemetryValue> = connectionManager.telemetry()
        .filter { it.pid.equals("0D", ignoreCase = true) }

    fun observeRpm(): Flow<TelemetryValue> = connectionManager.telemetry()
        .filter { it.pid.equals("0C", ignoreCase = true) }

    fun observeAll(): Flow<List<TelemetryValue>> = connectionManager.telemetry()
        .map { listOf(it) }
}
