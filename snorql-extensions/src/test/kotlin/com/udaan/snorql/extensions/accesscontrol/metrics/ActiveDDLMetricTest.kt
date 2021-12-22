package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.metrics.ActiveDDLMetric
import com.udaan.snorql.extensions.performance.models.ActiveDDLDTO
import com.udaan.snorql.extensions.performance.models.ActiveDDLInput
import com.udaan.snorql.extensions.performance.models.ActiveDDLResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class ActiveDDLMetricTest {
    companion object {
        private val activeDDLMetric = ActiveDDLMetric()
    }

    private val activeDDLMetricMainQuery: String? =
        activeDDLMetric.getMetricConfig(ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName").metricId).queries["main"]

    // Active DDL Query Input
    private val activeDDLInput1 =
        ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val activeDDLInput2 =
        ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val activeDDLInput3 = ActiveDDLInput(metricId = "randomIncorrectID", // Incorrect Metric ID passed
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName")
    private val activeDDLInput4 = ActiveDDLInput(metricId = "", // Empty string metricID
        metricPeriod = MetricPeriod.HISTORICAL,
        databaseName = "randomDatabaseName")

    // Random active ddl query 1
    private val activeDDLQuery1 = ActiveDDLDTO(currentStep = "1 Current Step",
        queryText = "SELECT randomColumn1 FROM randomTable1",
        totalRows = 51,
        rowsProcessed = 42,
        rowsLeft = 9,
        percentComplete = 78.42f,
        elapsedSeconds = 128,
        estimatedSecondsLeft = 184,
        estimatedCompletionTime = "21-12-2021 22:56:45")

    // Random active ddl query 2
    private val activeDDLQuery2 = ActiveDDLDTO(currentStep = "2 Current Step",
        queryText = "SELECT randomColumn2 FROM randomTable2",
        totalRows = 23,
        rowsProcessed = 12,
        rowsLeft = 11,
        percentComplete = 78.42f,
        elapsedSeconds = 123,
        estimatedSecondsLeft = 186,
        estimatedCompletionTime = "21-12-2021 22:58:45")

    // Mock ActiveDDLMetric Result
    private val activeDDLResult1 = ActiveDDLResult(listOf(activeDDLQuery1, activeDDLQuery2)) // Multiple queries
    private val activeDDLResult2 = ActiveDDLResult(listOf(activeDDLQuery1)) // Single Query
    private val activeDDLResult3 = ActiveDDLResult(listOf()) // No queries in result

    // Mock MetricOutput
    private val metricOutput1 = MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLResult1, null)
    private val metricOutput2 = MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLResult2, null)
    private val metricOutput3 = MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        // Expected getMetricResponseMetadata output 1
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(activeDDLMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )

        // Tests
        assertEquals(expected = expectedOutput1,
            activeDDLMetric.getMetricResponseMetadata(activeDDLInput1, metricOutput1))
        assertEquals(expected = expectedOutput1,
            activeDDLMetric.getMetricResponseMetadata(activeDDLInput2, metricOutput1))
        assertEquals(expected = expectedOutput1,
            activeDDLMetric.getMetricResponseMetadata(activeDDLInput1, metricOutput2))
        assertEquals(expected = expectedOutput1,
            activeDDLMetric.getMetricResponseMetadata(activeDDLInput1, metricOutput3))
        assertEquals(expected = expectedOutput1,
            activeDDLMetric.getMetricResponseMetadata(activeDDLInput2, metricOutput3))
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        val metricConfig1 = MetricConfig(
            queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )
        val metricConfig2 = MetricConfig(
            queries = mapOf(),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )

        activeDDLMetric.getMetricResult(activeDDLInput1, metricConfig1)
        activeDDLMetric.getMetricResult(activeDDLInput2, metricConfig2)
        activeDDLMetric.getMetricResult(activeDDLInput3, metricConfig1)
        activeDDLMetric.getMetricResult(activeDDLInput4, metricConfig2)
        activeDDLMetric.getMetricResponseMetadata(activeDDLInput3, metricOutput3)
        activeDDLMetric.getMetricResponseMetadata(activeDDLInput4, metricOutput3)
        activeDDLMetric.getMetricResponseMetadata(activeDDLInput3, metricOutput2)
        activeDDLMetric.getMetricResponseMetadata(activeDDLInput4, metricOutput1)

    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        val metricConfig1 = MetricConfig(
            queries = mapOf("main" to "SELECT randomColumn from randomTable"),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )
        val metricConfig2 = MetricConfig(
            queries = mapOf("main" to ""),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )
        val activeDDLMetricInput1 =
            ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
        val activeDDLMetricInput2 =
            ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
        activeDDLMetric.getMetricResult(activeDDLMetricInput1, metricConfig1)
        activeDDLMetric.getMetricResult(activeDDLMetricInput2, metricConfig1)
        activeDDLMetric.getMetricResult(activeDDLMetricInput1, metricConfig2)
        activeDDLMetric.getMetricResult(activeDDLMetricInput2, metricConfig2)
    }
}