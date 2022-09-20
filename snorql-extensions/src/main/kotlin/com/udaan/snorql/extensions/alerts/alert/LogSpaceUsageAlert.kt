package com.udaan.snorql.extensions.alerts.alert


import com.udaan.snorql.extensions.alerts.models.LogSpaceUsageAlertInput
import com.udaan.snorql.extensions.alerts.models.LogSpaceUsageAlertResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.LogSpaceUsageDTO
import com.udaan.snorql.extensions.performance.models.LogSpaceUsageInput
import com.udaan.snorql.extensions.performance.models.LogSpaceUsageResult
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod

class LogSpaceUsageAlert : IAlert<LogSpaceUsageAlertInput, LogSpaceUsageAlertResult, IAlertRecommendation> {
    override fun getAlertOutput(
        alertInput: LogSpaceUsageAlertInput,
        alertConfig: AlertConfigOutline
    ): AlertOutput<LogSpaceUsageAlertResult, IAlertRecommendation> {
        val metricOutput = SqlMetricManager.getMetric<LogSpaceUsageInput, LogSpaceUsageResult, IMetricRecommendation>(
            metricId = PerformanceEnums.LOG_SPACE_USAGE.getId(),
            metricInput = LogSpaceUsageInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = alertInput.databaseName
            )
        ).metricOutput as MetricOutput<LogSpaceUsageResult, IMetricRecommendation>

        val queryList: List<LogSpaceUsageDTO> = metricOutput.result.queryList
        val usagePercent = (queryList[0].spaceUsedInGB/queryList[0].maxSpaceInGB)*100
        val isAlert = usagePercent.toFloat() >= alertInput.logSpaceUsageLimitInPercent
        return AlertOutput(
            isAlert = isAlert,
            alertResult = LogSpaceUsageAlertResult(
                alertInstance = queryList[0]
            ),
            recommendation = null
        )
    }
}