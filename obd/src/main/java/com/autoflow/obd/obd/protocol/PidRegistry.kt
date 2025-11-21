package com.autoflow.obd.obd.protocol

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PidRegistry @Inject constructor(
    private val json: Json
) {
    private val mutableDefinitions = MutableStateFlow(buildBuiltin().associateBy { it.key })

    fun definitions(): StateFlow<Map<String, PidDefinition>> = mutableDefinitions

    fun find(mode: String, pid: String): PidDefinition? = mutableDefinitions.value["$mode$pid"]

    fun importPack(raw: String) {
        val pack = json.decodeFromString(PidPack.serializer(), raw)
        mutableDefinitions.update { current ->
            current + pack.pids.associateBy { it.key }
        }
    }

    private fun buildBuiltin(): List<PidDefinition> = listOf(
        PidDefinition(mode = "01", pid = "0C", bytes = 2, expression = "((A*256)+B)/4", unit = "rpm", min = 0.0, max = 8000.0, description = "Engine RPM", priority = 0),
        PidDefinition(mode = "01", pid = "0D", bytes = 1, expression = "A", unit = "km/h", min = 0.0, max = 255.0, description = "Vehicle Speed", priority = 0),
        PidDefinition(mode = "01", pid = "05", bytes = 1, expression = "A-40", unit = "°C", min = -40.0, max = 215.0, description = "Coolant Temp"),
        PidDefinition(mode = "01", pid = "0F", bytes = 1, expression = "A-40", unit = "°C", min = -40.0, max = 215.0, description = "Intake Air Temp"),
        PidDefinition(mode = "01", pid = "11", bytes = 1, expression = "(A*100)/255", unit = "%", min = 0.0, max = 100.0, description = "Throttle Position"),
        PidDefinition(mode = "01", pid = "10", bytes = 2, expression = "(((A*256)+B)/100)-327.68", unit = "g/s", min = 0.0, max = 200.0, description = "MAF"),
        PidDefinition(mode = "01", pid = "42", bytes = 2, expression = "((A*256)+B)/1000", unit = "V", min = 0.0, max = 20.0, description = "Control module voltage")
    )
}

@Serializable
data class PidPack(
    val version: Int = 1,
    @SerialName("region") val region: String = "global",
    val pids: List<PidDefinition>
)
