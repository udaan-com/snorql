package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.LogSpaceUsageDTO
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class LogSpaceUsageAlertInput(
    override val databaseName: String,
    val logSpaceUsageLimitInPercent: Float
) : AlertInput()

data class LogSpaceUsageAlertResult(
    val alertInstance: LogSpaceUsageDTO
) : IAlertResult()