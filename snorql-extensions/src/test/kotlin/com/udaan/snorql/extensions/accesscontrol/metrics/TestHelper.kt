package com.udaan.snorql.extensions.accesscontrol.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.MetricConfig
import org.mockito.ArgumentMatchers

object TestHelper {

    // Metric configs
    val metricConfigWithMainAndDbSizeQueries =
        MetricConfig(
            queries = mapOf("main" to "MetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutMainQuery =
        MetricConfig(
            queries = mapOf("notMain" to "MetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutDbSizeQuery =
        MetricConfig(
            queries = mapOf("main" to "MetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutMainAndDbSizeQueries =
        MetricConfig(
            queries = mapOf("notMain" to "MetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutQueries =
        MetricConfig(
            queries = mapOf(),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true
        )

    suspend inline fun <reified T> mockSqlMetricManager(
        databaseName: String, query: String, params: Map<String, *>, outputMetricList: List<T>
    ): SqlMetricManager {
//        val mockConnectionInstance: Connection = mock()
//        SqlMetricManager.setConnection(mockConnectionInstance)
        val sqlMetricManager: SqlMetricManager = mock()
        whenever(
            sqlMetricManager.queryExecutor.execute<T>(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString()
            )
        ).thenAnswer {
            val queryString = it.arguments[1]
            when {
                (queryString == metricConfigWithMainAndDbSizeQueries.queries["main"]) -> {
                    outputMetricList
                }
                (queryString == metricConfigWithMainAndDbSizeQueries.queries["dbSize"]) -> {
                    outputMetricList
                }
                else -> {
                    throw IllegalArgumentException("neither a main query nor db size query: $queryString")
                }
            }
        }
        return sqlMetricManager
    }
}