package com.autoflow.obd.obd.usecase

import com.autoflow.obd.core.security.LogEncryption
import com.autoflow.obd.data.db.PidSampleEntity
import com.autoflow.obd.data.repository.TripLogRepository
import com.autoflow.obd.data.preferences.UserPreferencesRepository
import com.autoflow.obd.obd.repository.ObdConnectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.toEpochMilliseconds
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripLoggingController @Inject constructor(
    private val connectionManager: ObdConnectionManager,
    private val repository: TripLogRepository,
    private val scope: CoroutineScope,
    private val preferencesRepository: UserPreferencesRepository
) {
    private var activeTripJob: Job? = null
    private var activeTripId: Long? = null
    private var encryptLogs = true

    init {
        scope.launch {
            preferencesRepository.preferences().collectLatest { prefs ->
                encryptLogs = prefs.encryptLogs
            }
        }
    }

    fun start(vin: String) {
        if (activeTripJob != null) return
        activeTripJob = scope.launch {
            val tripId = repository.startTrip(vin, System.currentTimeMillis())
            activeTripId = tripId
            connectionManager.telemetry().collect { telemetry ->
                val sample = PidSampleEntity(
                    tripId = tripId,
                    pidCode = telemetry.pid,
                    mode = telemetry.mode,
                    value = telemetry.value,
                    unit = telemetry.unit,
                    recordedAt = telemetry.timestamp.toEpochMilliseconds(),
                    rawPayload = LogEncryption.encrypt(telemetry.toString().encodeToByteArray(), encryptLogs)
                )
                repository.recordSamples(tripId, listOf(sample))
            }
        }
    }

    suspend fun stop() {
        activeTripJob?.cancel()
        activeTripId?.let { repository.endTrip(it, System.currentTimeMillis()) }
        activeTripJob = null
        activeTripId = null
    }
}
