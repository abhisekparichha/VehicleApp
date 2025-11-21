package com.autoflow.obd.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.autoflow.obd.core.locale.UnitPreferences
import com.autoflow.obd.core.locale.UnitSystem
import com.autoflow.obd.data.preferences.UserPreferencesRepository
import com.autoflow.obd.obd.repository.ObdConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserPreferencesRepository,
    private val connectionManager: ObdConnectionManager
) : ViewModel() {

    val preferences: StateFlow<UnitPreferences> = repository.preferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UnitPreferences())

    fun setUnit(system: UnitSystem) {
        viewModelScope.launch { repository.update { copy(system = system) } }
    }

    fun toggleNightMode(enabled: Boolean) {
        viewModelScope.launch { repository.update { copy(showNightMode = enabled) } }
    }

    fun toggleHud(mirrored: Boolean) {
        viewModelScope.launch { repository.update { copy(hudMirrored = mirrored) } }
    }

    fun toggleEncryption(enabled: Boolean) {
        viewModelScope.launch { repository.update { copy(encryptLogs = enabled) } }
    }

    fun toggleCloud(enabled: Boolean) {
        viewModelScope.launch { repository.update { copy(cloudUploadsEnabled = enabled) } }
    }

    fun sendRawCommand(command: String) {
        connectionManager.sendRaw(command)
    }
}
