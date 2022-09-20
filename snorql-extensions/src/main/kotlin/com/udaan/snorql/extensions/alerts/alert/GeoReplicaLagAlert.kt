package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.models.GeoReplicaLagAlertInput
import com.udaan.snorql.extensions.alerts.models.GeoReplicaLagAlertResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagDTO
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagInput
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagResult
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagRecommendation
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod


class GeoReplicaLagAlert : IAlert<GeoReplicaLagAlertInput, GeoReplicaLagAlertResult, IAlertRecommendation> {
    override fun getAlertOutput(
        alertInput: GeoReplicaLagAlertInput,
        alertConfig: AlertConfigOutline
    ): AlertOutput<GeoReplicaLagAlertResult, IAlertRecommendation> {
        val metricOutput = SqlMetricManager.getMetric<GeoReplicaLagInput,
                GeoReplicaLagResult, GeoReplicaLagRecommendation>(
                metricId = PerformanceEnums.GEO_REPLICA_LAG.getId(),
                metricInput = GeoReplicaLagInput(
                    metricPeriod = MetricPeriod.REAL_TIME,
                    databaseName = alertInput.databaseName,
                    primaryDatabaseName = alertInput.primaryDatabaseName
                )
            ).metricOutput as MetricOutput<GeoReplicaLagResult, GeoReplicaLagRecommendation>
        val queryList: List<GeoReplicaLagDTO> = metricOutput.result.queryList
        val replicationLag = queryList[0].replicationLagSec
        val isAlert = replicationLag >= alertInput.thresholdInSec
        return AlertOutput(
            isAlert = isAlert,
            alertResult = GeoReplicaLagAlertResult(
                alertInstance = queryList[0]
            ),
            recommendation = null
        )
    }

}