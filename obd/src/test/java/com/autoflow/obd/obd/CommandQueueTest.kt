package com.autoflow.obd.obd

import com.autoflow.obd.obd.command.CommandQueue
import com.autoflow.obd.obd.command.ObdCommand
import org.junit.Assert.assertEquals
import org.junit.Test

class CommandQueueTest {
    @Test
    fun `drains by priority`() {
        val queue = CommandQueue()
        queue.enqueue(ObdCommand(mode = "01", pid = "05", priority = 5))
        queue.enqueue(ObdCommand(mode = "01", pid = "0C", priority = 0))
        assertEquals("0C", queue.drainNext()?.pid)
    }
}
