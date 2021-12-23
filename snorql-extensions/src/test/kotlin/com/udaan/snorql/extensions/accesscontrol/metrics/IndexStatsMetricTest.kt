package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.metrics.IndexStatsMetric
import com.udaan.snorql.extensions.performance.models.BlockedQueriesInput
import com.udaan.snorql.extensions.performance.models.IndexStatDTO
import com.udaan.snorql.extensions.performance.models.IndexStatInput
import com.udaan.snorql.extensions.performance.models.IndexStatResult
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class IndexStatsMetricTest {
    companion object {
        val indexStatsMetric = IndexStatsMetric()
    }
    
    // Index Stat Metric Main Query String
    private val indexStatMetricMainQuery: String? =
        indexStatsMetric.getMetricConfig(IndexStatInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            indexName = "randomIndexName",
            tableName = "randomTableName").metricId).queries["main"]

    // Index Stats Metric Inputs
    private val indexStatsInput1 =
        IndexStatInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName",
            tableName = "randomTableName", indexName = "randomIndexName")
    private val indexStatsInput2 =
        IndexStatInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName",
            indexName = "randomIndexName")
    private val indexStatsInput3 =
        IndexStatInput(metricId = "incorrectMetricId",
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName",
            indexName = "randomIndexName")
    private val indexStatsInput4 =
        IndexStatInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName",
            tableName = "randomTableName", indexName = "randomIndexName")

    // Metric configs
    private val metricConfig1 = MetricConfig(    // "main" query not defined in config
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig2 = MetricConfig(    // empty queries map (no queries defined in config)
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig3 = MetricConfig(    // "main" query defined
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig4 = MetricConfig(    // empty "main" query
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // Index Statistics
    private val indexStats1 = IndexStatDTO(
        name = "indexName1",
        updated = "21-12-2021 22:08:44",
        rows = 233,
        rowsSampled = 190,
        steps = 23,
        density = 33,
        averageKeyLength = 23.44f,
        stringIndex = "Yes",
        filterExpression = "SomeExpression",
        unfilteredRows = 5,
        persistedSamplePercent = 33
    )
    private val indexStats2 =
        IndexStatDTO(name = null,
            updated = null,
            rows = null,
            rowsSampled = null,
            steps = null,
            density = null,
            averageKeyLength = null,
            stringIndex = null,
            filterExpression = null,
            unfilteredRows = null,
            persistedSamplePercent = null
        )

    // Index Stat Results
    private val indexStatResult1 = IndexStatResult(listOf(indexStats1, indexStats2)) // Multiple stats in result
    private val indexStatResult2 = IndexStatResult(listOf(indexStats2))
    private val indexStatResult3 = IndexStatResult(listOf()) // empty result

    // Index Stat Output
    private val metricOutput1 = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatResult1, null)
    private val metricOutput2 = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatResult2, null)
    private val metricOutput3 = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(indexStatMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput1,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput1,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput1,
                metricOutput3))
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput2,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput2,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(indexStatsInput2,
                metricOutput3))
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput1, metricConfig = metricConfig1)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput2, metricConfig = metricConfig1)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput1, metricConfig = metricConfig2)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput2, metricConfig = metricConfig2)
        indexStatsMetric.getMetricResponseMetadata(metricInput = indexStatsInput3, metricOutput = metricOutput1)
        indexStatsMetric.getMetricResponseMetadata(metricInput = indexStatsInput4, metricOutput = metricOutput1)
        indexStatsMetric.getMetricResponseMetadata(metricInput = indexStatsInput3, metricOutput = metricOutput2)
        indexStatsMetric.getMetricResponseMetadata(metricInput = indexStatsInput4, metricOutput = metricOutput2)
    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput1, metricConfig = metricConfig3)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput2, metricConfig = metricConfig3)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput1, metricConfig = metricConfig4)
        indexStatsMetric.getMetricResult(metricInput = indexStatsInput2, metricConfig = metricConfig4)
    }
}