package com.autoflow.obd.adapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.core.connectivity.AdapterSummary
import com.autoflow.obd.obd.repository.ObdConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AdapterScanViewModel @Inject constructor(
    private val connectionManager: ObdConnectionManager
) : ViewModel() {

    val adapters: StateFlow<List<AdapterSummary>> = connectionManager.adapters()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        connectionManager.scanAdapters()
    }

    fun connect(summary: AdapterSummary) {
        val endpoint = connectionManager.endpointFromSummary(summary)
        connectionManager.connect(endpoint)
    }
}
