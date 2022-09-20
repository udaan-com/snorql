package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.ActiveQueryResult
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class ActiveQueriesFilterAlertInput(
    override val databaseName: String,
    val queriesCountThreshold: Int?,
    val elapsedTimeThreshold: Int?,
    val cpuTimeThreshold: Int?,
    val logicalReadsThreshold: Int?,
    val readsThreshold: Int?,
    val writesThreshold: Int?,
    val openTransactionCountThreshold: Int?
) : AlertInput()

data class ActiveQueriesFilterAlertResult(
    val filteredMetricResult: ActiveQueryResult
) : IAlertResult()