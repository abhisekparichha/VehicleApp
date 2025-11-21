package com.autoflow.obd.obd.protocol

import com.autoflow.obd.core.model.TelemetryStatus
import com.autoflow.obd.core.model.TelemetryValue

object ObdResponseParser {
    fun parse(frame: String, registry: PidRegistry): TelemetryValue? {
        val clean = frame.trim().replace(" ", "").uppercase()
        if (clean.length < 6) return null
        if (!clean.startsWith("41")) return null
        val pid = clean.substring(2, 4)
        val definition = registry.find("01", pid) ?: return null
        val payloadHex = clean.substring(4)
        val bytes = payloadHex.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
        val value = ExpressionEvaluator.evaluate(definition.expression, bytes)
        val status = when {
            value >= definition.max -> TelemetryStatus.CRITICAL
            value >= definition.max * 0.8 -> TelemetryStatus.CAUTION
            else -> TelemetryStatus.NORMAL
        }
        return TelemetryValue(
            pid = definition.pid,
            mode = definition.mode,
            label = definition.description,
            value = value,
            unit = definition.unit,
            status = status
        )
    }
}
