package com.autoflow.obd.obd.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.autoflow.obd.core.connectivity.AdapterKind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothObdAdapter @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val scope: CoroutineScope
) : ObdAdapter {
    override lateinit var endpoint: AdapterEndpoint
    private var socket: BluetoothSocket? = null
    private val framesFlow = MutableSharedFlow<ObdFrame>(replay = 0, extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun connect() {
        val target = (endpoint as? BluetoothEndpoint)?.macAddress
            ?: throw IllegalStateException("Bluetooth endpoint required")
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(target)
        val uuid = UUID.fromString(SPP_UUID)
        socket = device.createRfcommSocketToServiceRecord(uuid)
        bluetoothAdapter.cancelDiscovery()
        socket?.connect()
        startReader()
        sendCommand("ATZ")
        sendCommand("ATE0")
        sendCommand("ATL0")
    }

    private fun startReader() {
        val input = socket?.inputStream ?: return
        val reader = BufferedReader(InputStreamReader(input))
        scope.launch(Dispatchers.IO) {
            while (isActive && socket?.isConnected == true) {
                val line = reader.readLine() ?: break
                if (line.isNotBlank()) {
                    framesFlow.emit(ObdFrame(raw = line.trim()))
                }
            }
        }
    }

    override suspend fun disconnect() {
        socket?.close()
        socket = null
    }

    override fun isConnected(): Boolean = socket?.isConnected == true

    override suspend fun sendCommand(command: String) {
        val stream: OutputStream = socket?.outputStream ?: return
        val payload = "$command\r"
        stream.write(payload.toByteArray())
        stream.flush()
    }

    override fun frames(): Flow<ObdFrame> = framesFlow

    override suspend fun healthCheck(): AdapterStatus {
        sendCommand("ATI")
        return AdapterStatus(
            firmware = "ELM327-compatible",
            protocol = AdapterKind.BLUETOOTH_CLASSIC.name,
            voltage = 0.0
        )
    }

    companion object {
        private const val SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB"
    }
}
