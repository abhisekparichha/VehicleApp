package com.autoflow.obd.features.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.data.db.TripLogEntity
import com.autoflow.obd.data.repository.TripLogRepository
import com.autoflow.obd.obd.usecase.TripLoggingController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val repository: TripLogRepository,
    private val tripLoggingController: TripLoggingController
) : ViewModel() {

    private val mutableState = MutableStateFlow(LogsUiState())
    val state: StateFlow<LogsUiState> = mutableState

    init {
        viewModelScope.launch {
            repository.trips().collectLatest { trips ->
                mutableState.value = mutableState.value.copy(trips = trips)
            }
        }
    }

    fun toggleLogging() {
        if (mutableState.value.isLogging) {
            viewModelScope.launch {
                tripLoggingController.stop()
                mutableState.value = mutableState.value.copy(isLogging = false)
            }
        } else {
            tripLoggingController.start(vin = "MOCKVIN123")
            mutableState.value = mutableState.value.copy(isLogging = true)
        }
    }
}

data class LogsUiState(
    val trips: List<TripLogEntity> = emptyList(),
    val isLogging: Boolean = false
)
