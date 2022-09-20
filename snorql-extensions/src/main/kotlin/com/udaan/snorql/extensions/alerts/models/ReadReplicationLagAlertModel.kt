package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.ReadReplicationLagDTO
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class ReadReplicationLagAlertInput(
    override val databaseName: String,
    val replicaDbName: String,
    val thresholdInSec: Int
) : AlertInput()

data class ReadReplicationLagAlertResult(
    val alertInstance: ReadReplicationLagDTO
) : IAlertResult()
