package com.autoflow.obd.core.locale

enum class UnitSystem {
    METRIC,
    IMPERIAL
}

data class RegionDefaults(
    val localeTag: String,
    val unitSystem: UnitSystem
)

data class UnitPreferences(
    val system: UnitSystem = UnitSystem.METRIC,
    val showNightMode: Boolean = true,
    val hudMirrored: Boolean = false,
    val encryptLogs: Boolean = true,
    val cloudUploadsEnabled: Boolean = false
)
