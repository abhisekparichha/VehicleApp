package com.autoflow.obd.obd.adapter

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
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class WifiObdAdapter @Inject constructor(
    private val scope: CoroutineScope
) : ObdAdapter {
    override lateinit var endpoint: AdapterEndpoint
    private var socket: Socket? = null
    private val framesFlow = MutableSharedFlow<ObdFrame>(replay = 0, extraBufferCapacity = 32, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override suspend fun connect() {
        val wifi = endpoint as? WifiEndpoint ?: error("Wi-Fi endpoint required")
        socket = Socket()
        socket?.connect(InetSocketAddress(wifi.host, wifi.port), 5000)
        startReader()
    }

    private fun startReader() {
        val input = socket?.getInputStream() ?: return
        val reader = BufferedReader(InputStreamReader(input))
        scope.launch(Dispatchers.IO) {
            while (isActive && socket?.isConnected == true) {
                val line = reader.readLine() ?: break
                if (line.isNotBlank()) framesFlow.emit(ObdFrame(raw = line.trim()))
            }
        }
    }

    override suspend fun disconnect() {
        socket?.close()
        socket = null
    }

    override fun isConnected(): Boolean = socket?.isConnected == true

    override suspend fun sendCommand(command: String) {
        val stream: OutputStream = socket?.getOutputStream() ?: return
        stream.write("$command\r".toByteArray())
        stream.flush()
    }

    override fun frames(): Flow<ObdFrame> = framesFlow

    override suspend fun healthCheck(): AdapterStatus {
        sendCommand("ATI")
        return AdapterStatus(
            firmware = "Wi-Fi adapter",
            protocol = "TCP",
            voltage = 0.0
        )
    }
}
