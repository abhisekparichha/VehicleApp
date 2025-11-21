package com.autoflow.obd.data.repository

import com.autoflow.obd.data.db.DriveSafeDao
import com.autoflow.obd.data.db.PidSampleEntity
import com.autoflow.obd.data.db.TripLogEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripLogRepository @Inject constructor(
    private val dao: DriveSafeDao
) {
    suspend fun startTrip(vehicleVin: String, startTime: Long): Long =
        dao.insertTrip(TripLogEntity(vehicleVin = vehicleVin, startTime = startTime, endTime = null, routeGeoJson = null))

    suspend fun endTrip(tripId: Long, endTime: Long) = dao.endTrip(tripId, endTime)

    suspend fun recordSamples(tripId: Long, samples: List<PidSampleEntity>) =
        dao.recordSamples(tripId, samples)

    fun trips() = dao.observeTrips()
}
