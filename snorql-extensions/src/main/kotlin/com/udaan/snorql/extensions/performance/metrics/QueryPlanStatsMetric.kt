package com.udaan.snorql.extensions.performance.metrics

import com.udaan.snorql.extensions.performance.models.QueryPlanStatsDTO
import com.udaan.snorql.extensions.performance.models.QueryPlanStatsInput
import com.udaan.snorql.extensions.performance.models.QueryPlanStatsResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

/**
 * Class which implements the Query Plan Statistics metric
 *
 * <p>The Query Plan Statistic can be used to get information around the estimated execution
 * plan prepared by the sql engine</p>
 *
 * @constructor Create Query Plan stats metric
 */
class QueryPlanStatsMetric: IMetric<QueryPlanStatsInput, QueryPlanStatsResult, IMetricRecommendation> {

    override fun getMetricResult(metricInput: QueryPlanStatsInput, metricConfig: MetricConfig): QueryPlanStatsResult {
        val initialQuery =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        val preHooks = metricConfig.queries["pre"] ?: ""
        val postHooks = metricConfig.queries["post"] ?: ""


        val processedQuery = initialQuery.replace(
            "<QUERY>",
            metricInput.query
        )

        val results = SqlMetricManager.queryExecutor.execute<QueryPlanStatsDTO>(
            databaseName = metricInput.databaseName,
            query = processedQuery,
            mapClass = QueryPlanStatsDTO::class.java,
            preHooks = {
                this.execute(preHooks)
            },
            postHooks = {
                this.execute(postHooks)
            }
        )
        return QueryPlanStatsResult(results)
    }

    override fun getMetricResponseMetadata(
        metricInput: QueryPlanStatsInput,
        metricOutput: MetricOutput<QueryPlanStatsResult, IMetricRecommendation>
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
