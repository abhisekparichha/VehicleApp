package com.autoflow.obd.obd.protocol

object DtcDecoder {
    fun decode(frame: String): List<String> {
        val clean = frame.replace(" ", "").uppercase()
        if (!clean.startsWith("43") || clean.length < 4) return emptyList()
        val payload = clean.substring(2)
        return payload.chunked(4).mapNotNull { chunk ->
            if (chunk.length < 4) return@mapNotNull null
            val bytes = chunk.chunked(2).map { it.toInt(16) }
            val first = bytes.getOrNull(0) ?: return@mapNotNull null
            val second = bytes.getOrNull(1) ?: return@mapNotNull null
            if (first == 0 && second == 0) return@mapNotNull null
            val firstChar = when ((first and 0xC0) shr 6) {
                0 -> 'P'
                1 -> 'C'
                2 -> 'B'
                else -> 'U'
            }
            val secondDigit = ((first and 0x30) shr 4)
            val thirdDigit = (first and 0x0F)
            val code = "$firstChar$secondDigit$thirdDigit${second.toString(16).padStart(2, '0').uppercase()}"
            code
        }
    }
}
