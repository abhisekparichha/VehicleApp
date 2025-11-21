package com.autoflow.obd.data.repository

import com.autoflow.obd.data.db.DashboardLayoutEntity
import com.autoflow.obd.data.db.DriveSafeDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val dao: DriveSafeDao
) {
    fun dashboards(): Flow<List<DashboardLayoutEntity>> = dao.observeDashboards()

    suspend fun save(layoutJson: String, name: String) {
        dao.saveDashboard(DashboardLayoutEntity(name = name, payloadJson = layoutJson))
    }
}
