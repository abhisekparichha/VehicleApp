package com.autoflow.obd.data.repository

import com.autoflow.obd.data.db.DriveSafeDao
import com.autoflow.obd.data.db.VehicleEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val dao: DriveSafeDao
) {
    fun vehicles() = dao.observeVehicles()

    suspend fun save(vehicle: VehicleEntity) = dao.upsertVehicle(vehicle)
}
