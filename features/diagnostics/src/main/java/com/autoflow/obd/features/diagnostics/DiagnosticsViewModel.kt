package com.autoflow.obd.features.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.core.safety.ParkedActionGate
import com.autoflow.obd.obd.command.CommandQueue
import com.autoflow.obd.obd.command.ObdCommand
import com.autoflow.obd.obd.repository.ObdConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val connectionManager: ObdConnectionManager,
    private val commandQueue: CommandQueue,
    private val parkedActionGate: ParkedActionGate
) : ViewModel() {

    private val mutableState = MutableStateFlow(DiagnosticsUiState())
    val state: StateFlow<DiagnosticsUiState> = mutableState

    init {
        viewModelScope.launch {
            combine(
                connectionManager.dtcStream(),
                parkedActionGate.canPerformDestructiveActions()
            ) { dtcs, canClear ->
                mutableState.value.copy(dtcList = dtcs, canClear = canClear)
            }.collect { mutableState.value = it }
        }
    }

    fun readCodes() {
        commandQueue.enqueue(ObdCommand(mode = "03", pid = "00", priority = 0))
    }

    fun clearCodes() {
        if (!mutableState.value.canClear) return
        commandQueue.enqueue(ObdCommand(mode = "04", pid = "00", priority = 0))
    }
}

data class DiagnosticsUiState(
    val dtcList: List<String> = emptyList(),
    val canClear: Boolean = false
)
