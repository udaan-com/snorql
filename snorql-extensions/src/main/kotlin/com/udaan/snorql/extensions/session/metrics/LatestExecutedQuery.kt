package com.udaan.snorql.extensions.session.metrics

import com.udaan.snorql.extensions.session.models.LatestExecutedQueryDTO
import com.udaan.snorql.extensions.session.models.LatestExecutedQueryInput
import com.udaan.snorql.extensions.session.models.LatestExecutedQueryResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class LatestExecutedQuery : IMetric<LatestExecutedQueryInput, LatestExecutedQueryResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: LatestExecutedQueryInput,
        metricConfig: MetricConfig
    ): LatestExecutedQueryResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config " +
                        "[${metricInput.metricId}]")
        val paramMap = mapOf("sessionIdParam" to metricInput.sessionId)
        val result = SqlMetricManager.queryExecutor.execute<LatestExecutedQueryDTO>(
            metricInput.databaseName,
            query,
            params = paramMap
        )
        return LatestExecutedQueryResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: LatestExecutedQueryInput,
        metricOutput: MetricOutput<LatestExecutedQueryResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description
        responseMetadata["supportsHistorical"] = metricConfig.supportsHistorical
        responseMetadata["minimumRepeatInterval"] = metricConfig.persistDataOptions?.get("minimumRepeatInterval") ?: ""

        return responseMetadata
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
