package com.udaan.snorql.extensions.performance.metrics

import com.udaan.snorql.extensions.performance.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

/**
 * Class which implements the Query Plan XML Metric
 *
 * <p>The Query Plan XML can be used to get access to the XML plan generated by the engine</p>
 *
 * @constructor Create Query Plan XML metric
 */
class QueryPlanXMLMetric: IMetric<QueryPlanXMLInput, QueryPlanXMLResult, IMetricRecommendation> {

    override fun getMetricResult(metricInput: QueryPlanXMLInput, metricConfig: MetricConfig): QueryPlanXMLResult {
        val initialQuery =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        val preHooks = metricConfig.queries["pre"] ?: ""
        val postHooks = metricConfig.queries["post"] ?: ""


        val processedQuery = initialQuery.replace(
            "<QUERY>",
            metricInput.query
        )

        val results = SqlMetricManager.queryExecutor.execute<QueryPlanXMLModelDTO>(
            databaseName = metricInput.databaseName,
            query = processedQuery,
            mapClass = QueryPlanXMLModelDTO::class.java,
            preHooks = {
                this.execute(preHooks)
            },
            postHooks = {
                this.execute(postHooks)
            }
        )
        return QueryPlanXMLResult(results)
    }

    override fun getMetricResponseMetadata(
        metricInput: QueryPlanXMLInput,
        metricOutput: MetricOutput<QueryPlanXMLResult, IMetricRecommendation>
    ): Map<String, Any> {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]?.replace("<QUERY>", metricInput.query)
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description

        return responseMetadata
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
