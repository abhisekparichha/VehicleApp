package com.autoflow.obd.obd

import com.autoflow.obd.obd.protocol.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpressionEvaluatorTest {
    @Test
    fun `evaluates rpm formula`() {
        val bytes = byteArrayOf(0x1A, 0xF8.toByte())
        val value = ExpressionEvaluator.evaluate("((A*256)+B)/4", bytes)
        assertEquals(1726.0, value, 0.01)
    }
}
