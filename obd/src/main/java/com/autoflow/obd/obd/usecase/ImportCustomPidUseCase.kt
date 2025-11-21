package com.autoflow.obd.obd.usecase

import com.autoflow.obd.obd.protocol.PidRegistry
import javax.inject.Inject

class ImportCustomPidUseCase @Inject constructor(
    private val registry: PidRegistry
) {
    fun execute(rawJson: String) = registry.importPack(rawJson)
}
