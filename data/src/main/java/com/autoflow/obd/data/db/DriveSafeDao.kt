package com.autoflow.obd.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DriveSafeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles ORDER BY nickname")
    fun observeVehicles(): Flow<List<VehicleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(tripLogEntity: TripLogEntity): Long

    @Query("UPDATE trip_logs SET endTime = :end WHERE id = :tripId")
    suspend fun endTrip(tripId: Long, end: Long)

    @Query("SELECT * FROM trip_logs ORDER BY startTime DESC")
    fun observeTrips(): Flow<List<TripLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSamples(samples: List<PidSampleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDtc(event: DtcEventEntity)

    @Query("SELECT * FROM dtc_events WHERE vehicleVin = :vin ORDER BY occurredAt DESC")
    fun observeDtc(vin: String): Flow<List<DtcEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDashboard(layoutEntity: DashboardLayoutEntity)

    @Query("SELECT * FROM dashboards ORDER BY createdAt DESC")
    fun observeDashboards(): Flow<List<DashboardLayoutEntity>>

    @Transaction
    suspend fun recordSamples(tripId: Long, samples: List<PidSampleEntity>) {
        if (samples.isNotEmpty()) insertSamples(samples)
    }
}
