package com.autoflow.obd.obd.command

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.PriorityQueue
import javax.inject.Inject
import javax.inject.Singleton

 data class ObdCommand(
    val mode: String,
    val pid: String,
    val priority: Int = 1,
    val payload: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Singleton
class CommandQueue @Inject constructor() {
    private val queue = PriorityQueue(compareBy<ObdCommand> { it.priority }.thenBy { it.createdAt })
    private val events = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    @Synchronized
    fun enqueue(command: ObdCommand) {
        queue.add(command)
        events.tryEmit(Unit)
    }

    @Synchronized
    fun drainNext(): ObdCommand? = queue.poll()

    fun onEnqueued(): SharedFlow<Unit> = events
}
