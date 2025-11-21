package com.autoflow.obd.core.model

data class VehicleProfile(
    val vin: String,
    val nickname: String,
    val fuelType: FuelType,
    val transmission: TransmissionType,
    val manufacturer: String,
    val model: String,
    val year: Int,
    val supportsCan: Boolean = true
)

enum class FuelType { PETROL, DIESEL, HYBRID, EV }

enum class TransmissionType { MANUAL, AUTOMATIC, CVT }
