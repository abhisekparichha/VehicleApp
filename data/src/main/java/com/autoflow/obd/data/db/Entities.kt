package com.autoflow.obd.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val vin: String,
    val nickname: String,
    val manufacturer: String,
    val model: String,
    val year: Int,
    val fuelType: String,
    val transmission: String
)

@Entity(
    tableName = "trip_logs",
    indices = [Index(value = ["vehicleVin"])],
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["vin"],
            childColumns = ["vehicleVin"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TripLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleVin: String,
    val startTime: Long,
    val endTime: Long?,
    val routeGeoJson: String?
)

@Entity(
    tableName = "pid_samples",
    indices = [Index(value = ["tripId", "pidCode"])]
)
data class PidSampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val pidCode: String,
    val mode: String,
    val value: Double,
    val unit: String,
    val recordedAt: Long,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val rawPayload: ByteArray? = null
)

@Entity(tableName = "dtc_events")
data class DtcEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleVin: String,
    val code: String,
    val description: String,
    val severity: String,
    val occurredAt: Long,
    val clearedAt: Long? = null
)

@Entity(tableName = "dashboards")
data class DashboardLayoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val payloadJson: String,
    val createdAt: Long = Instant.DISTANT_PAST.toEpochMilliseconds()
)
