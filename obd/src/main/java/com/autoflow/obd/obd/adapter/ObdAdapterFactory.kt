package com.autoflow.obd.obd.adapter

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ObdAdapterFactory @Inject constructor(
    private val bluetoothProvider: Provider<BluetoothObdAdapter>,
    private val bleProvider: Provider<BleObdAdapter>,
    private val wifiProvider: Provider<WifiObdAdapter>,
    private val mockAdapter: Provider<MockObdAdapter>
) {
    fun create(endpoint: AdapterEndpoint): ObdAdapter = when (endpoint) {
        is BluetoothEndpoint -> bluetoothProvider.get().apply { this.endpoint = endpoint }
        is BleEndpoint -> bleProvider.get().apply { this.endpoint = endpoint }
        is WifiEndpoint -> wifiProvider.get().apply { this.endpoint = endpoint }
        is MockEndpoint -> mockAdapter.get().apply { }
        else -> mockAdapter.get()
    }
}
