package com.autoflow.obd.core.connectivity

enum class AdapterKind {
    BLUETOOTH_CLASSIC,
    BLE,
    WIFI,
    USB,
    MOCK
}

data class AdapterSummary(
    val id: String,
    val name: String,
    val kind: AdapterKind,
    val rssi: Int? = null,
    val protocolHint: String? = null,
    val firmware: String? = null,
    val lastSeenMs: Long = System.currentTimeMillis()
)
