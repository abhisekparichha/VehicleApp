package com.autoflow.obd.core.safety

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class DrivingState {
    PARKED, IDLING, MOVING
}

@Singleton
class DrivingStateMonitor @Inject constructor() {
    private val mutableState = MutableStateFlow(DrivingState.PARKED)

    fun observe(): Flow<DrivingState> = mutableState.asStateFlow()

    fun updateFromSpeed(kph: Double) {
        val next = when {
            kph < 1.0 -> DrivingState.PARKED
            kph < 5.0 -> DrivingState.IDLING
            else -> DrivingState.MOVING
        }
        if (next != mutableState.value) {
            mutableState.value = next
        }
    }
}
