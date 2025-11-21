package com.autoflow.obd.core.security

object LogEncryption {
    private const val SEED: Int = 0x55

    fun encrypt(input: ByteArray, enabled: Boolean): ByteArray {
        if (!enabled) return input
        return input.mapIndexed { index, byte ->
            (byte.toInt() xor (SEED + index)).toByte()
        }.toByteArray()
    }

    fun decrypt(input: ByteArray, enabled: Boolean): ByteArray = encrypt(input, enabled)
}
