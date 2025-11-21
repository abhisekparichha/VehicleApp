package com.autoflow.obd.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        VehicleEntity::class,
        TripLogEntity::class,
        PidSampleEntity::class,
        DtcEventEntity::class,
        DashboardLayoutEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class DriveSafeDatabase : RoomDatabase() {
    abstract fun driveSafeDao(): DriveSafeDao
}
