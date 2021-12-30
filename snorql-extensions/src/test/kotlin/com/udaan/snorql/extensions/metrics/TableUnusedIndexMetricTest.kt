package com.udaan.snorql.extensions.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.metrics.TableUnusedIndexMetric
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexDTO
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexInput
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import junit.framework.Assert.assertEquals
import junit.framework.Assert.fail
import org.junit.Test
import kotlin.test.assertEquals

class TableUnusedIndexMetricTest {
    companion object {
        private val tableUnusedIndexMetric = TableUnusedIndexMetric()
    }

    // Table Schema Metric Query String
    private val tableUnusedIndexMetricMainQuery: String? = tableUnusedIndexMetric.getMetricConfig(
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName1"
        ).metricId
    ).queries["main"]

    // Table Schema Metric Stats
    private val tableUnusedIndexResult1 = TableUnusedIndexDTO(
        objectName = "ObjectName1",
        indexName = "IndexName1",
        userSeeks = 20,
        userScans = 10,
        userLookups = 5,
        userUpdates = 12,
        columnName = "ColumnName1"

    )
    private val tableUnusedIndexResult2 = TableUnusedIndexDTO(
        objectName = "ObjectName2",
        indexName = "IndexName2",
        userSeeks = 20,
        userScans = 10,
        userLookups = 5,
        userUpdates = 12,
        columnName = "ColumnName2"
    )

    // Table Schema Metric Inputs
    private val tableUnusedIndexMetricInputHistorical1 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            tableName = "randomTableName1"
        )
    private val tableUnusedIndexMetricInputRealTime1 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName1",
            tableName = "randomTableName1"
        )
    private val tableUnusedIndexMetricInputHistorical2 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName2",
            tableName = "randomTableName2"
        )
    private val tableUnusedIndexMetricInputRealTime2 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName2",
            tableName = "randomTableName2"
        )
    private val tableUnusedIndexMetricInputHistorical3 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName3",
            tableName = "randomTableName3"
        )
    private val tableUnusedIndexMetricInputRealTime3 =
        TableUnusedIndexInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName3",
            tableName = "randomTableName3"
        )
    private val tableUnusedIndexMetricInputIncorrectMetricId = TableUnusedIndexInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName1",
        tableName = "randomTableName1"
    )
    private val tableUnusedIndexMetricInputEmptyMetricId =
        TableUnusedIndexInput(
            metricId = "",
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            tableName = ""
        )

    // Table Schema Metric Result
    private val tableUnusedIndexMetricResultMultipleResults =
        TableUnusedIndexResult(listOf(tableUnusedIndexResult1, tableUnusedIndexResult2))
    private val tableUnusedIndexMetricResultSingleResult = TableUnusedIndexResult(listOf(tableUnusedIndexResult1))
    private val tableUnusedIndexMetricResultEmptyResult = TableUnusedIndexResult(listOf()) // empty result

    // Table Schema Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<TableUnusedIndexResult, IMetricRecommendation>(tableUnusedIndexMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<TableUnusedIndexResult, IMetricRecommendation>(tableUnusedIndexMetricResultSingleResult, null)
    private val metricOutputEmptyResult =
        MetricOutput<TableUnusedIndexResult, IMetricRecommendation>(tableUnusedIndexMetricResultEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(tableUnusedIndexMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputRealTime1,
                metricOutputMultipleResults
            )
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputHistorical1,
                metricOutputSingleResult
            )
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputRealTime2,
                metricOutputEmptyResult
            )
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputRealTime1,
                metricOutputMultipleResults
            )
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputRealTime1,
                metricOutputSingleResult
            )
        )
        assertEquals(
            expected = expectedOutput1,
            tableUnusedIndexMetric.getMetricResponseMetadata(
                tableUnusedIndexMetricInputRealTime1,
                metricOutputEmptyResult
            )
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in listOf(
            tableUnusedIndexMetricInputIncorrectMetricId,
            tableUnusedIndexMetricInputEmptyMetricId
        )) {
            for (metricOutput in metricOutputList) {
                try {
                    tableUnusedIndexMetric.getMetricResponseMetadata(
                        metricInput = metricInput,
                        metricOutput = metricOutput
                    )
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
        // Check for failing test cases throwing SQLMonitoringConnectionException
        for (metricInput in listOf(
            tableUnusedIndexMetricInputRealTime1,
            tableUnusedIndexMetricInputRealTime2,
            tableUnusedIndexMetricInputHistorical1,
            tableUnusedIndexMetricInputHistorical2
        )) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutDbSizeQuery
            )) {
                try {
                    tableUnusedIndexMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                    fail("Test did not throw an Exception for:\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                } catch (e: SQLMonitoringConnectionException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }

        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val metricInputList = listOf(
            tableUnusedIndexMetricInputRealTime1,
            tableUnusedIndexMetricInputRealTime2,
            tableUnusedIndexMetricInputRealTime3,
            tableUnusedIndexMetricInputHistorical1,
            tableUnusedIndexMetricInputHistorical2,
            tableUnusedIndexMetricInputHistorical3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                tableUnusedIndexMetric.executeQuery<TableUnusedIndexDTO>(
                    databaseName = metricInput.databaseName,
                    queryString = "MetricMainQuery",
                    params = mapOf("tableName" to metricInput.tableName)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<TableUnusedIndexDTO>(tableUnusedIndexResult1, tableUnusedIndexResult2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(tableUnusedIndexResult1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<TableUnusedIndexDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            tableUnusedIndexMetricResultMultipleResults,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputRealTime1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableUnusedIndexMetricResultSingleResult,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputRealTime2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableUnusedIndexMetricResultEmptyResult,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputRealTime3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableUnusedIndexMetricResultMultipleResults,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputHistorical1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableUnusedIndexMetricResultSingleResult,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableUnusedIndexMetricResultEmptyResult,
            tableUnusedIndexMetric.getMetricResult(
                tableUnusedIndexMetricInputHistorical3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in listOf(
            tableUnusedIndexMetricInputIncorrectMetricId,
            tableUnusedIndexMetricInputEmptyMetricId
        )) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutQueries,
                TestHelper.metricConfigWithoutMainQuery,
                TestHelper.metricConfigWithEmptyStringMainQuery
            )) {
                try {
                    tableUnusedIndexMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                    fail("Test did not throw an Exception for: \nMetric Input: $metricInput\nMetric Config: $metricConfig")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }
        reset(mockConnection)
    }
}