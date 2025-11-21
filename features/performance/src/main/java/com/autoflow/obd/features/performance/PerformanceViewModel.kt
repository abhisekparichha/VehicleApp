package com.autoflow.obd.features.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.obd.usecase.DashboardTelemetryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val telemetryUseCase: DashboardTelemetryUseCase
) : ViewModel() {

    private val mutableState = kotlinx.coroutines.flow.MutableStateFlow(PerformanceUiState())
    val state: kotlinx.coroutines.flow.StateFlow<PerformanceUiState> = mutableState
    private var timerJob: Job? = null

    fun startZeroToSixty() {
        if (timerJob != null) return
        timerJob = viewModelScope.launch {
            var startTime: Long? = null
            telemetryUseCase.observeSpeed().collect { reading ->
                val speed = reading.value
                if (speed < 1 && startTime == null) {
                    startTime = System.currentTimeMillis()
                }
                if (startTime != null && speed >= 96.0) {
                    val duration = System.currentTimeMillis() - startTime!!
                    mutableState.value = mutableState.value.copy(zeroToSixtyMs = duration)
                    timerJob?.cancel()
                }
            }
        }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        mutableState.value = PerformanceUiState()
    }
}

data class PerformanceUiState(
    val zeroToSixtyMs: Long? = null
)
