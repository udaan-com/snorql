package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.metrics.BlockedQueriesMetric
import com.udaan.snorql.extensions.performance.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.fail

class BlockedQueriesMetricTest {
    companion object {
        val blockedQueriesMetric = BlockedQueriesMetric()
    }

    // to get the main query string
    private val blockedQueriesMetricMainQuery: String? = blockedQueriesMetric.getMetricConfig(
        BlockedQueriesInput(
            metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName"
        ).metricId
    ).queries["main"]

    // Blocked Queries inputs
    private val blockedQueriesInput1 =
        BlockedQueriesInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val blockedQueriesInput2 =
        BlockedQueriesInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val blockedQueriesInput3 =
        BlockedQueriesInput(
            metricId = "randomMetricID",
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName"
        )
    private val blockedQueriesInput4 =
        BlockedQueriesInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")

    // Random blocked queries
    private val blockedQuery1 = BlockedQueriesDTO(
        sessionId = 1234,
        status = "Running",
        blockedBy = 4321,
        waitType = null,
        waitResource = null,
        waitTime = "48 seconds",
        cpuTime = 66,
        logicalReads = 34,
        reads = 45,
        writes = 55,
        elapsedTime = "34 seconds",
        queryText = "SELECT randomColumn FROM randomTable",
        storedProc = "Some stored procedure",
        command = "Some random command",
        loginName = "Login Name",
        hostName = "Some Host Name",
        programName = "Some Program name",
        hostProcessId = 345,
        lastRequestEndTime = "78 seconds ago",
        loginTime = "22:45:59",
        openTransactionCount = 1,
        batchText = "Some batch text",
        blockingThese = null,
        inputBuffer = "Some input buffer"
    )

    private val blockedQuery2 = BlockedQueriesDTO(
        sessionId = 4321,
        status = "Running",
        blockedBy = 1234,
        waitType = null,
        waitResource = null,
        waitTime = "48 seconds",
        cpuTime = 66,
        logicalReads = 34,
        reads = 45,
        writes = 55,
        elapsedTime = "34 seconds",
        queryText = "SELECT randomColumn FROM randomTable",
        storedProc = "Some stored procedure",
        command = "Some random command",
        loginName = "Login Name",
        hostName = "Some Host Name",
        programName = "Some Program name",
        hostProcessId = 345,
        lastRequestEndTime = "78 seconds ago",
        loginTime = "22:45:59",
        openTransactionCount = 1,
        batchText = "Some batch text",
        blockingThese = null,
        inputBuffer = "Some input buffer",
        blockingTree = mutableListOf(blockedQuery1)
    )

    // Blocked Query Results
    private val blockedQueryResult1 = BlockedQueriesResult(listOf(blockedQuery1, blockedQuery2))
    private val blockedQueryResult2 = BlockedQueriesResult(listOf(blockedQuery1))
    private val blockedQueryResult3 = BlockedQueriesResult(listOf()) // No queries in result

    // Active Query Metric Outputs
    private val metricOutput1 = MetricOutput<BlockedQueriesResult, IMetricRecommendation>(blockedQueryResult1, null)
    private val metricOutput2 = MetricOutput<BlockedQueriesResult, IMetricRecommendation>(blockedQueryResult2, null)
    private val metricOutput3 = MetricOutput<BlockedQueriesResult, IMetricRecommendation>(blockedQueryResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(blockedQueriesMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput1)
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput2)
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput3)
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput1)
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput2)
        )
        assertEquals(
            expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput3)
        )

        for (metricInput in listOf(
            blockedQueriesInput3,
            blockedQueriesInput4
        )) {
            for (metricOutput in listOf(metricOutput1, metricOutput2, metricOutput3)) {
                try {
                    blockedQueriesMetric.getMetricResponseMetadata(
                        metricInput = metricInput,
                        metricOutput = metricOutput
                    )
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricOutput")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricInput")
                }
            }
        }
    }

    @Test
    fun testGetMetricResult() {
        // Testing SQLMonitoringConfigException
        for (metricInput in listOf(blockedQueriesInput1, blockedQueriesInput2)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutMainQuery,
                TestHelper.metricConfigWithoutQueries
            )) {
                try {
                    blockedQueriesMetric.getMetricResult(metricInput, metricConfig)
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricConfig")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }

        for (metricInput in listOf(blockedQueriesInput1, blockedQueriesInput2)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithMainAndDbSizeQueries,
                TestHelper.metricConfigWithEmptyStringMainQuery
            )) {
                try {
                    blockedQueriesMetric.getMetricResult(metricInput, metricConfig)
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricConfig")
                } catch (e: SQLMonitoringConnectionException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }
    }
}