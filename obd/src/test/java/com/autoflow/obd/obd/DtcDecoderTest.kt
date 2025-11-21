package com.autoflow.obd.obd

import com.autoflow.obd.obd.protocol.DtcDecoder
import org.junit.Assert.assertTrue
import org.junit.Test

class DtcDecoderTest {
    @Test
    fun `decodes dtc codes`() {
        val codes = DtcDecoder.decode("43 01 33 00 00")
        assertTrue(codes.contains("P0133"))
    }
}
