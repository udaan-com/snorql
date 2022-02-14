package com.udaan.snorql.extensions.session.metrics

import com.udaan.snorql.extensions.session.models.SessionLocksDTO
import com.udaan.snorql.extensions.session.models.SessionLocksInput
import com.udaan.snorql.extensions.session.models.SessionLocksResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

class SessionLocksMetric : IMetric<SessionLocksInput, SessionLocksResult, IMetricRecommendation> {

    override fun getMetricResult(metricInput: SessionLocksInput, metricConfig: MetricConfig): SessionLocksResult {
        val query = metricConfig.queries["main"]
            ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")
        val result = SqlMetricManager.queryExecutor.execute<SessionLocksDTO>(
            databaseName = metricInput.databaseName,
            query = query,
            params = mapOf("sessionId" to metricInput.sessionId)
        )
        return SessionLocksResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: SessionLocksInput,
        metricOutput: MetricOutput<SessionLocksResult, IMetricRecommendation>
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