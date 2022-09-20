package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.GeoReplicaLagDTO
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagDTO
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class GeoReplicaLagAlertInput(
    override val databaseName: String,
    val primaryDatabaseName: String,
    val thresholdInSec: Int
) : AlertInput()

data class GeoReplicaLagAlertResult(
    val alertInstance: GeoReplicaLagDTO
) : IAlertResult()
