package com.udaan.snorql.extensions.accesscontrol.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.storage.metrics.PVSMetric
import com.udaan.snorql.extensions.storage.models.PVSDTO
import com.udaan.snorql.extensions.storage.models.PVSInput
import com.udaan.snorql.extensions.storage.models.PVSResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class PVSMetricTest {
    companion object {
        private val pvsMetric = PVSMetric()
    }

    // DB Metric Query String
    private val pvsMetricMainQuery: String? = pvsMetric.getMetricConfig(
        PVSInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName"
        ).metricId
    ).queries["main"]

    // Database Table Stats
    private val randompvsMetric1 = PVSDTO(
        persistentVersionStoreSizeGb = 2,
        onlineIndexVersionStoreSizeGb = 1,
        currentAbortedTransactionCount = 22,
        abortedVersionCleanerStartTime = "2021-07-23 13:10:11",
        abortedVersionCleanerEndTime = "2021-07-23 13:12:11",
        oldestTransactionBeginTime = "2021-11-12 16:12:11",
        activeTransactionSessionId = 1234,
        activeTransactionElapsedTimeSeconds = 1220
    )
    private val randompvsMetric2 = PVSDTO(
        persistentVersionStoreSizeGb = null,
        onlineIndexVersionStoreSizeGb = null,
        currentAbortedTransactionCount = null,
        abortedVersionCleanerStartTime = null,
        abortedVersionCleanerEndTime = null,
        oldestTransactionBeginTime = null,
        activeTransactionSessionId = null,
        activeTransactionElapsedTimeSeconds = null
    )

    // PVS Metric Inputs
    private val pvsMetricInputHistorical1 =
        PVSInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1")
    private val pvsMetricInputRealTime1 =
        PVSInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val pvsMetricInputHistorical2 =
        PVSInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val pvsMetricInputRealTime2 =
        PVSInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName2")
    private val pvsMetricInputHistorical3 =
        PVSInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3")
    private val pvsMetricInputRealTime3 =
        PVSInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3")
    private val pvsMetricInputIncorrectMetricId = PVSInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName1"
    )
    private val pvsMetricInputEmptyMetricId =
        PVSInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1")

    // PVS Metric Result
    private val pvsMetricResultMultipleResults = PVSResult(listOf(randompvsMetric1, randompvsMetric2))
    private val pvsMetricResultSingleResult = PVSResult(listOf(randompvsMetric1))
    private val pvsMetricResultEmptyResult = PVSResult(listOf()) // empty result

    // PVS Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<PVSResult, IMetricRecommendation>(pvsMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<PVSResult, IMetricRecommendation>(pvsMetricResultSingleResult, null)
    private val metricOutputEmptyResult =
        MetricOutput<PVSResult, IMetricRecommendation>(pvsMetricResultEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(pvsMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputHistorical1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputRealTime2, metricOutputEmptyResult)
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputRealTime1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            pvsMetric.getMetricResponseMetadata(pvsMetricInputRealTime1, metricOutputEmptyResult)
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in listOf(pvsMetricInputIncorrectMetricId, pvsMetricInputEmptyMetricId)) {
            for (metricOutput in metricOutputList) {
                try {
                    pvsMetric.getMetricResponseMetadata(metricInput = metricInput, metricOutput = metricOutput)
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
            pvsMetricInputRealTime1,
            pvsMetricInputRealTime2,
            pvsMetricInputHistorical1,
            pvsMetricInputHistorical2
        )) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutDbSizeQuery
            )) {
                try {
                    pvsMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                } catch (e: SQLMonitoringConnectionException) {
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
                pvsMetric.executeQuery<PVSDTO>(
                    databaseName = databaseName, // "randomDatabaseName1", // "randomDatabaseName1",
                    queryString = "MetricMainQuery",
                    // params = any()// "MetricMainQuery" // "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<PVSDTO>(randompvsMetric1, randompvsMetric2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(randompvsMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<PVSDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            pvsMetricResultMultipleResults,
            pvsMetric.getMetricResult(
                pvsMetricInputRealTime1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            pvsMetricResultSingleResult,
            pvsMetric.getMetricResult(
                pvsMetricInputRealTime2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            pvsMetricResultEmptyResult,
            pvsMetric.getMetricResult(
                pvsMetricInputRealTime3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            pvsMetricResultMultipleResults,
            pvsMetric.getMetricResult(
                pvsMetricInputHistorical1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        println(pvsMetric.getMetricResult(
            pvsMetricInputHistorical1,
            TestHelper.metricConfigWithMainAndDbSizeQueries
        ))
        assertEquals(
            pvsMetricResultSingleResult,
            pvsMetric.getMetricResult(
                pvsMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            pvsMetricResultEmptyResult,
            pvsMetric.getMetricResult(
                pvsMetricInputHistorical3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in listOf(pvsMetricInputIncorrectMetricId, pvsMetricInputEmptyMetricId)) {
            for (metricConfig in listOf(TestHelper.metricConfigWithoutMainAndDbSizeQueries, TestHelper.metricConfigWithoutQueries, TestHelper.metricConfigWithoutMainQuery, TestHelper.metricConfigWithEmptyStringMainQuery)) {
                try {
                    pvsMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }
    }
}