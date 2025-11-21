package com.autoflow.obd.obd.adapter

import com.autoflow.obd.core.connectivity.AdapterKind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

class MockObdAdapter @Inject constructor(
    private val scope: CoroutineScope
) : ObdAdapter {
    override val endpoint: AdapterEndpoint = MockEndpoint()

    private var connected = false
    private val framesFlow = MutableSharedFlow<ObdFrame>(replay = 0, extraBufferCapacity = 16)

    override suspend fun connect() {
        connected = true
        scope.launch {
            var rpm = 900.0
            while (isActive && connected) {
                rpm = (rpm + (-150..150).random()).coerceIn(700.0, 5500.0)
                emitPid("410C${formatHex((rpm * 4).toInt(), 4)}")
                emitPid("410D${formatHex((40..120).random(), 2)}")
                emitPid("4105${formatHex((80..110).random(), 2)}")
                delay(250)
            }
        }
    }

    override suspend fun disconnect() {
        connected = false
    }

    override fun isConnected(): Boolean = connected

    override suspend fun sendCommand(command: String) {
        // Echo the command as a success frame for UI feedback
        framesFlow.emit(ObdFrame(raw = "ACK:$command"))
    }

    override fun frames(): Flow<ObdFrame> = framesFlow

    override suspend fun healthCheck(): AdapterStatus = AdapterStatus(
        firmware = "ELM327 v2.3 (mock)",
        protocol = "ISO 15765-4 (CAN)",
        voltage = 12.1
    )

    private suspend fun emitPid(payload: String) {
        framesFlow.emit(ObdFrame(raw = payload))
    }

    private fun formatHex(value: Int, width: Int): String = value.toString(16).padStart(width, '0').uppercase()
}
