package com.udaan.snorql.extensions.accesscontrol.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.performance.metrics.ActiveDDLMetric
import com.udaan.snorql.extensions.performance.models.ActiveDDLDTO
import com.udaan.snorql.extensions.performance.models.ActiveDDLInput
import com.udaan.snorql.extensions.performance.models.ActiveDDLResult
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.fail

class ActiveDDLMetricTest {
    companion object {
        private val activeDDLMetric = ActiveDDLMetric()
    }

    private val activeDDLMetricMainQuery: String? =
        activeDDLMetric.getMetricConfig(ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName").metricId).queries["main"]

    // Active DDL Query Input
    private val activeDDLInput1 =
        ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val activeDDLInput2 =
        ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val activeDDLInput3 = ActiveDDLInput(metricId = "randomIncorrectID", // Incorrect Metric ID passed
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName")
    private val activeDDLInput4 = ActiveDDLInput(metricId = "", // Empty string metricID
        metricPeriod = MetricPeriod.HISTORICAL,
        databaseName = "randomDatabaseName")
    private val activeDDLInput5 =
        ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3")

    // Metric Configs
    val metricConfig3 = MetricConfig(
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    val metricConfig4 = MetricConfig(
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
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

        for (metricInput in listOf(activeDDLInput1, activeDDLInput2)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2, metricOutput3)) {
                assertEquals(expected = expectedOutput1,
                    activeDDLMetric.getMetricResponseMetadata(metricInput, metricOutput))
            }
        }

        for (metricInput in listOf(activeDDLInput3, activeDDLInput4)) {
            for (metricOutput in listOf(metricOutput3, metricOutput2, metricOutput1)) {
                try {
                    activeDDLMetric.getMetricResponseMetadata(metricInput, metricOutput)
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricOutput")
                }
            }
        }
    }

    @Test
    fun testGetMetricResult() {
        // Testing for SQLMonitoringConnectionException
        for (metricInput in listOf(activeDDLInput1, activeDDLInput2)) {
            for (metricConfig in listOf(metricConfig1, metricConfig2)) {
                try {
                    activeDDLMetric.getMetricResult(metricInput, metricConfig)
                } catch (e: SQLMonitoringConnectionException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(activeDDLInput1, activeDDLInput2, activeDDLInput3, activeDDLInput4)) {
            for (metricConfig in listOf(metricConfig3, metricConfig4)) {
                try {
                    activeDDLMetric.getMetricResult(metricInput, metricConfig)
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }

        SqlMetricManager.setConnection(mock())
        val databaseNames = listOf("randomDatabaseName1", "randomDatabaseName2", "randomDatabaseName3")
        databaseNames.forEach { databaseName ->
            whenever(
                activeDDLMetric.executeQuery<ActiveDDLDTO>(
                    databaseName = databaseName,
                    queryString = "MetricMainQuery",
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf(activeDDLQuery1, activeDDLQuery2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(activeDDLQuery1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<ActiveDDLDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(activeDDLResult1, activeDDLMetric.getMetricResult(activeDDLInput1, TestHelper.metricConfigWithMainAndDbSizeQueries))
        assertEquals(activeDDLResult2, activeDDLMetric.getMetricResult(activeDDLInput2, TestHelper.metricConfigWithMainAndDbSizeQueries))
        assertEquals(activeDDLResult3, activeDDLMetric.getMetricResult(activeDDLInput5, TestHelper.metricConfigWithMainAndDbSizeQueries))

    }
}