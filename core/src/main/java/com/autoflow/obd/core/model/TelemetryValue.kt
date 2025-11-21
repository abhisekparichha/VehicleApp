package com.autoflow.obd.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

 data class TelemetryValue(
    val pid: String,
    val mode: String,
    val label: String,
    val value: Double,
    val unit: String,
    val status: TelemetryStatus,
    val timestamp: Instant = Clock.System.now()
)

enum class TelemetryStatus { NORMAL, CAUTION, CRITICAL }
