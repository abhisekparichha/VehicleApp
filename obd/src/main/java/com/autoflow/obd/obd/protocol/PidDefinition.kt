package com.autoflow.obd.obd.protocol

import kotlinx.serialization.Serializable

@Serializable
data class PidDefinition(
    val mode: String,
    val pid: String,
    val bytes: Int,
    val expression: String,
    val unit: String,
    val min: Double,
    val max: Double,
    val description: String,
    val priority: Int = 1
) {
    val key: String get() = "$mode$pid"
}
