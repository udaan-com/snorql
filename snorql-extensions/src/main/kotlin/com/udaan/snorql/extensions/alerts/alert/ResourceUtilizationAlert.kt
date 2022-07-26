package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.models.ResourceThresholdParameter
import com.udaan.snorql.extensions.alerts.models.ResourceUtilInput
import com.udaan.snorql.extensions.alerts.models.ResourceUtilResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.ComputeUtilizationInput
import com.udaan.snorql.extensions.performance.models.ComputeUtilizationResult
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.metric.SqlMetricManager.logger
import com.udaan.snorql.framework.models.*
import java.time.LocalDateTime
import java.time.ZoneId

class ResourceUtilizationAlert : IAlert<ResourceUtilInput, ResourceUtilResult, IAlertRecommendation> {
    override fun getAlertOutput(
        alertInput: ResourceUtilInput,
        alertConfig: AlertConfigOutline
    ): AlertOutput<ResourceUtilResult, IAlertRecommendation> {
        val tempDatabaseName = if (alertInput.configuredOnReplica) {
            logger.info("[ResourceUtilizationAlert] Alert is configured on replica: {}", alertConfig)
            alertInput.databaseName + "-readonly"
        } else {
            logger.info("[ResourceUtilizationAlert] Alert is configured on primary database: {}", alertConfig)
            alertInput.databaseName
        }
        val metricResponse =
            SqlMetricManager.getMetric<ComputeUtilizationInput, ComputeUtilizationResult, IMetricRecommendation>(
                metricId = PerformanceEnums.COMPUTE_UTILIZATION.getId(),
                metricInput = ComputeUtilizationInput(
                    metricId = PerformanceEnums.COMPUTE_UTILIZATION.getId(),
                    metricPeriod = MetricPeriod.REAL_TIME,
                    databaseName = tempDatabaseName
                )
            )
        val resourceValueList =
            (metricResponse as MetricResponse<ComputeUtilizationResult, IMetricRecommendation>).metricOutput.result.queryList
        val timeThreshold = getTimeThreshold(alertConfig.watchIntervalInSeconds)
        val instancesInTime = resourceValueList.filter {
            val instanceTimestamp = LocalDateTime.parse((it.timeId).replace(" ", "T"))
            timeThreshold <= instanceTimestamp
        }
        val filteredQueryList = when (alertInput.resourceType) {
            ResourceThresholdParameter.CPU -> instancesInTime.filter { alertInput.resourceUtilizationThreshold <= it.maxCpuPercent }
            ResourceThresholdParameter.DATA_IO -> instancesInTime.filter { alertInput.resourceUtilizationThreshold <= it.maxDataIoPercent }
            ResourceThresholdParameter.MEMORY -> instancesInTime.filter { alertInput.resourceUtilizationThreshold <= it.maxMemoryPercent }
            ResourceThresholdParameter.LOG_IO -> instancesInTime.filter { alertInput.resourceUtilizationThreshold <= it.maxLogIoPercent }
//            else -> throw ResourceTypeNotFound("${alertInput.resourceType} is not a supported resource type in ResourceUtilizationAlert")
        }
        val isAlert = filteredQueryList.isNotEmpty()

        return AlertOutput(
            isAlert = isAlert, alertResult = ResourceUtilResult(
                alertResourceType = alertInput.resourceType, alertInstances = filteredQueryList
            ), recommendation = null
        )

    }

    private fun getTimeThreshold(watchIntervalInSeconds: Int): LocalDateTime {
        val currentTime = LocalDateTime.now(ZoneId.of("UTC"))
        val roundingOff = currentTime.minusMinutes(currentTime.minute % 5.toLong())
        return roundingOff.minusSeconds(watchIntervalInSeconds.toLong() + 300).withSecond(0)
    }

}