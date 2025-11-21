package com.autoflow.obd.obd.adapter

import com.autoflow.obd.core.connectivity.AdapterKind
import kotlinx.coroutines.flow.Flow

sealed interface AdapterEndpoint {
    val id: String
    val name: String
    val kind: AdapterKind
}

data class BluetoothEndpoint(
    override val id: String,
    override val name: String,
    val macAddress: String
) : AdapterEndpoint {
    override val kind: AdapterKind = AdapterKind.BLUETOOTH_CLASSIC
}

data class BleEndpoint(
    override val id: String,
    override val name: String
) : AdapterEndpoint {
    override val kind: AdapterKind = AdapterKind.BLE
}

data class WifiEndpoint(
    override val id: String,
    override val name: String,
    val host: String,
    val port: Int
) : AdapterEndpoint {
    override val kind: AdapterKind = AdapterKind.WIFI
}

data class MockEndpoint(
    override val id: String = "mock",
    override val name: String = "Mock Adapter"
) : AdapterEndpoint {
    override val kind: AdapterKind = AdapterKind.MOCK
}

data class AdapterStatus(
    val firmware: String,
    val protocol: String,
    val voltage: Double
)

data class ObdFrame(
    val raw: String,
    val timestampMs: Long = System.currentTimeMillis()
)

interface ObdAdapter {
    val endpoint: AdapterEndpoint

    suspend fun connect()
    suspend fun disconnect()
    fun isConnected(): Boolean
    suspend fun sendCommand(command: String)
    fun frames(): Flow<ObdFrame>
    suspend fun healthCheck(): AdapterStatus
}
