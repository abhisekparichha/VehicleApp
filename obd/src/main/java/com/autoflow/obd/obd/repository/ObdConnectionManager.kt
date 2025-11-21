package com.autoflow.obd.obd.repository

import com.autoflow.obd.core.connectivity.AdapterKind
import com.autoflow.obd.core.connectivity.AdapterSummary
import com.autoflow.obd.core.model.TelemetryValue
import com.autoflow.obd.core.safety.DrivingStateMonitor
import com.autoflow.obd.core.utils.DispatcherProvider
import com.autoflow.obd.obd.adapter.AdapterEndpoint
import com.autoflow.obd.obd.adapter.BleEndpoint
import com.autoflow.obd.obd.adapter.BluetoothEndpoint
import com.autoflow.obd.obd.adapter.MockEndpoint
import com.autoflow.obd.obd.adapter.ObdAdapter
import com.autoflow.obd.obd.adapter.ObdAdapterFactory
import com.autoflow.obd.obd.adapter.WifiEndpoint
import com.autoflow.obd.obd.command.CommandQueue
import com.autoflow.obd.obd.command.ObdCommand
import com.autoflow.obd.obd.protocol.DtcDecoder
import com.autoflow.obd.obd.protocol.ObdResponseParser
import com.autoflow.obd.obd.protocol.PidRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObdConnectionManager @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val scope: CoroutineScope,
    private val commandQueue: CommandQueue,
    private val registry: PidRegistry,
    private val adapterFactory: ObdAdapterFactory,
    private val drivingStateMonitor: DrivingStateMonitor
) {
    private val telemetryFlow = MutableSharedFlow<TelemetryValue>(replay = 1)
    private val stateFlow = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    private val adapterSummaryFlow = MutableStateFlow<List<AdapterSummary>>(emptyList())
    private val dtcFlow = MutableSharedFlow<List<String>>(replay = 1, extraBufferCapacity = 1)
    private var activeAdapter: ObdAdapter? = null

    fun telemetry(): Flow<TelemetryValue> = telemetryFlow.asSharedFlow()

    fun state(): StateFlow<ConnectionState> = stateFlow.asStateFlow()

    fun adapters(): StateFlow<List<AdapterSummary>> = adapterSummaryFlow.asStateFlow()

    fun dtcStream(): Flow<List<String>> = dtcFlow.asSharedFlow()

    fun endpointFromSummary(summary: AdapterSummary): AdapterEndpoint = when (summary.kind) {
        AdapterKind.BLUETOOTH_CLASSIC -> BluetoothEndpoint(
            id = summary.id,
            name = summary.name,
            macAddress = summary.id
        )
        AdapterKind.BLE -> BleEndpoint(summary.id, summary.name)
        AdapterKind.WIFI -> WifiEndpoint(summary.id, summary.name, host = summary.id, port = 35000)
        AdapterKind.USB -> MockEndpoint(summary.id, summary.name)
        AdapterKind.MOCK -> MockEndpoint(summary.id, summary.name)
    }

    fun scanAdapters() {
        scope.launch(dispatcherProvider.io) {
            val mockList = listOf(
                AdapterSummary(id = "mock", name = "Mock Adapter", kind = AdapterKind.MOCK, rssi = -30, protocolHint = "CAN"),
                AdapterSummary(id = "AA:BB:CC:DD:EE:FF", name = "Bluetooth OBD", kind = AdapterKind.BLUETOOTH_CLASSIC, rssi = -55, protocolHint = "CAN"),
                AdapterSummary(id = "192.168.0.10", name = "Wi-Fi OBD", kind = AdapterKind.WIFI, rssi = null, protocolHint = "ISO 15765")
            )
            adapterSummaryFlow.emit(mockList)
        }
    }

    fun connect(endpoint: AdapterEndpoint) {
        scope.launch(dispatcherProvider.io) {
            stateFlow.emit(ConnectionState.Connecting(endpoint))
            val adapter = adapterFactory.create(endpoint)
            adapter.connect()
            activeAdapter = adapter
            stateFlow.emit(ConnectionState.Connected(endpoint))
            startReaders(adapter)
            primeCommandLoop()
        }
    }

    fun disconnect() {
        scope.launch(dispatcherProvider.io) {
            activeAdapter?.disconnect()
            stateFlow.emit(ConnectionState.Disconnected)
        }
    }

    fun sendRaw(command: String) {
        scope.launch(dispatcherProvider.io) {
            activeAdapter?.sendCommand(command)
        }
    }

    private fun startReaders(adapter: ObdAdapter) {
        scope.launch(dispatcherProvider.io) {
            adapter.frames().collect { frame ->
                ObdResponseParser.parse(frame.raw, registry)?.let { telemetry ->
                    telemetryFlow.emit(telemetry)
                    if (telemetry.label.contains("Speed", true)) {
                        drivingStateMonitor.updateFromSpeed(telemetry.value)
                    }
                }
                if (frame.raw.trim().startsWith("43")) {
                    dtcFlow.emit(DtcDecoder.decode(frame.raw))
                }
            }
        }
    }

    private fun primeCommandLoop() {
        scope.launch(dispatcherProvider.io) {
            val essential = listOf("0C", "0D", "05", "11")
            while (isActive && stateFlow.value is ConnectionState.Connected) {
                essential.forEachIndexed { index, pid ->
                    commandQueue.enqueue(ObdCommand(mode = "01", pid = pid, priority = index))
                }
                drainQueue()
                delay(500)
            }
        }
    }

    private suspend fun drainQueue() {
        var command = commandQueue.drainNext()
        while (command != null && activeAdapter != null) {
            val payload = buildCommand(command)
            activeAdapter?.sendCommand(payload)
            command = commandQueue.drainNext()
        }
    }

    private fun buildCommand(command: ObdCommand): String =
        buildString {
            append(command.mode)
            append(' ')
            append(command.pid)
            if (command.payload.isNotBlank()) {
                append(' ')
                append(command.payload)
            }
        }

    fun mockTelemetryStream(): Flow<TelemetryValue> = flow {
        while (true) {
            commandQueue.enqueue(ObdCommand("01", "0C", priority = 0))
            delay(1000)
        }
    }
}

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data class Connecting(val endpoint: AdapterEndpoint) : ConnectionState
    data class Connected(val endpoint: AdapterEndpoint) : ConnectionState
}
