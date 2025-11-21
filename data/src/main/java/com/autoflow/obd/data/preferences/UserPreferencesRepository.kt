package com.autoflow.obd.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.autoflow.obd.core.locale.UnitPreferences
import com.autoflow.obd.core.locale.UnitSystem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val UNIT_KEY = stringPreferencesKey("unit_system")
    private val NIGHT_KEY = booleanPreferencesKey("night_mode")
    private val HUD_MIRROR_KEY = booleanPreferencesKey("hud_mirror")
    private val ENCRYPT_KEY = booleanPreferencesKey("encrypt_logs")
    private val CLOUD_KEY = booleanPreferencesKey("cloud_uploads")

    fun preferences(): Flow<UnitPreferences> = dataStore.data.map { prefs ->
        UnitPreferences(
            system = UnitSystem.valueOf(prefs[UNIT_KEY] ?: UnitSystem.METRIC.name),
            showNightMode = prefs[NIGHT_KEY] ?: true,
            hudMirrored = prefs[HUD_MIRROR_KEY] ?: false,
            encryptLogs = prefs[ENCRYPT_KEY] ?: true,
            cloudUploadsEnabled = prefs[CLOUD_KEY] ?: false
        )
    }

    suspend fun update(block: UnitPreferences.() -> UnitPreferences) {
        dataStore.edit { prefs ->
            val current = UnitPreferences(
                system = UnitSystem.valueOf(prefs[UNIT_KEY] ?: UnitSystem.METRIC.name),
                showNightMode = prefs[NIGHT_KEY] ?: true,
                hudMirrored = prefs[HUD_MIRROR_KEY] ?: false,
                encryptLogs = prefs[ENCRYPT_KEY] ?: true,
                cloudUploadsEnabled = prefs[CLOUD_KEY] ?: false
            )
            val updated = block(current)
            prefs[UNIT_KEY] = updated.system.name
            prefs[NIGHT_KEY] = updated.showNightMode
            prefs[HUD_MIRROR_KEY] = updated.hudMirrored
            prefs[ENCRYPT_KEY] = updated.encryptLogs
            prefs[CLOUD_KEY] = updated.cloudUploadsEnabled
        }
    }
}
