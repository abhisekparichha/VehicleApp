package com.autoflow.obd.core.safety

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParkedActionGate @Inject constructor(
    private val drivingStateMonitor: DrivingStateMonitor
) {
    fun canPerformDestructiveActions(): Flow<Boolean> =
        drivingStateMonitor.observe().map { it == DrivingState.PARKED }
}
