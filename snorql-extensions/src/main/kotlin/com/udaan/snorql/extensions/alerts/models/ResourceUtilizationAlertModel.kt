package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.ComputeUtilizationDTO
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class ResourceUtilInput(
    override val databaseName: String,
    val resourceType: ResourceThresholdParameter,
    val resourceUtilizationThreshold: Float,
    val configuredOnReplica: Boolean
) : AlertInput()

data class ResourceUtilResult(
    val alertResourceType: ResourceThresholdParameter,
    val alertInstances: List<ComputeUtilizationDTO>
) : IAlertResult()

enum class ResourceThresholdParameter {
    CPU, DATA_IO, MEMORY, LOG_IO;
}

//class ResourceTypeNotFound(override val message: String) : Exception()

