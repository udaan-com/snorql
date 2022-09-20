package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.models.ReadReplicationLagAlertInput
import com.udaan.snorql.extensions.alerts.models.ReadReplicationLagAlertResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagDTO
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagInput
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagResult
import com.udaan.snorql.extensions.performance.models.ReadReplicationRecommendation
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod


class ReadReplicationLagAlert : IAlert<
        ReadReplicationLagAlertInput, ReadReplicationLagAlertResult, IAlertRecommendation> {
    override fun getAlertOutput(
        alertInput: ReadReplicationLagAlertInput,
        alertConfig: AlertConfigOutline
    ): AlertOutput<ReadReplicationLagAlertResult, IAlertRecommendation> {
        val metricOutput = SqlMetricManager.getMetric<ReadReplicationLagInput,
                ReadReplicationLagResult, ReadReplicationRecommendation>(
                metricId = PerformanceEnums.READ_REPLICATION_LAG.getId(),
                metricInput = ReadReplicationLagInput(
                    metricPeriod = MetricPeriod.REAL_TIME,
                    databaseName = alertInput.databaseName,
                    replicaDatabaseName = alertInput.replicaDbName
                )
            ).metricOutput as MetricOutput<ReadReplicationLagResult, ReadReplicationRecommendation>
        val queryList: List<ReadReplicationLagDTO> = metricOutput.result.queryList
        val replicationLag = queryList[0].replicationLagInMillis/1000
        val isAlert = replicationLag >= alertInput.thresholdInSec
        return AlertOutput(
            isAlert = isAlert,
            alertResult = ReadReplicationLagAlertResult(
                alertInstance = queryList[0]
            ),
            recommendation = null
        )
    }

}