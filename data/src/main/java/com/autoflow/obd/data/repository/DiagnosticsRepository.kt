package com.autoflow.obd.data.repository

import com.autoflow.obd.data.db.DriveSafeDao
import com.autoflow.obd.data.db.DtcEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticsRepository @Inject constructor(
    private val dao: DriveSafeDao
) {
    fun observeDtc(vin: String): Flow<List<DtcEventEntity>> = dao.observeDtc(vin)

    suspend fun recordDtc(event: DtcEventEntity) = dao.insertDtc(event)
}
