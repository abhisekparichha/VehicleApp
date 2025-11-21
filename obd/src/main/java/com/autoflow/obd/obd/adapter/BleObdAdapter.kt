package com.autoflow.obd.obd.adapter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.autoflow.obd.core.connectivity.AdapterKind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class BleObdAdapter @Inject constructor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope
) : ObdAdapter {
    override lateinit var endpoint: AdapterEndpoint
    private var gatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private val frames = MutableSharedFlow<ObdFrame>(extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun connect() {
        val device = bluetoothAdapter.getRemoteDevice((endpoint as BleEndpoint).id)
        gatt = device.connectGatt(context, false, callback)
    }

    override suspend fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    override fun isConnected(): Boolean = gatt != null

    override suspend fun sendCommand(command: String) {
        val characteristic = writeCharacteristic ?: return
        characteristic.setValue("$command\r")
        gatt?.writeCharacteristic(characteristic)
    }

    override fun frames(): Flow<ObdFrame> = frames

    override suspend fun healthCheck(): AdapterStatus = AdapterStatus(
        firmware = "BLE Serial",
        protocol = AdapterKind.BLE.name,
        voltage = 0.0
    )

    private val callback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.services.find { it.uuid == NUS_SERVICE }
            val notifyChar = service?.getCharacteristic(NUS_RX)
            val writeChar = service?.getCharacteristic(NUS_TX)
            writeCharacteristic = writeChar
            gatt.setCharacteristicNotification(notifyChar, true)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            scope.launch { frames.emit(ObdFrame(raw = value.decodeToString())) }
        }
    }

    companion object {
        private val NUS_SERVICE: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        private val NUS_RX: UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
        private val NUS_TX: UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    }
}
