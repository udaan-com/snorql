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
import kotlin.test.assertEquals

class BlockedQueriesMetricTest {
    companion object {
        val blockedQueriesMetric = BlockedQueriesMetric()
    }

    // to get the main query string
    private val blockedQueriesMetricMainQuery: String? = blockedQueriesMetric.getMetricConfig(BlockedQueriesInput(
        metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName").metricId).queries["main"]

    // Blocked Queries inputs
    private val blockedQueriesInput1 =
        BlockedQueriesInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val blockedQueriesInput2 =
        BlockedQueriesInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val blockedQueriesInput3 =
        BlockedQueriesInput(metricId = "randomMetricID",
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName")
    private val blockedQueriesInput4 =
        BlockedQueriesInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")

    // Random blocked queries
    private val blockedQuery1 = BlockedQueriesDTO(sessionId = 1234,
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

    private val blockedQuery2 = BlockedQueriesDTO(sessionId = 4321,
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
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput1))
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput2))
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput1, metricOutput3))
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput1))
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput2))
        assertEquals(expected = expectedOutput1,
            blockedQueriesMetric.getMetricResponseMetadata(blockedQueriesInput2, metricOutput3))
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        // "main" query not defined in config
        val metricConfig1 = MetricConfig(
            queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )

        // empty queries map (no queries defined in config)
        val metricConfig2 = MetricConfig(
            queries = mapOf(),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput1,
            metricConfig = metricConfig1)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput2,
            metricConfig = metricConfig1)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput1,
            metricConfig = metricConfig2)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput2,
            metricConfig = metricConfig2)
        blockedQueriesMetric.getMetricResponseMetadata(metricInput = blockedQueriesInput1, metricOutput = metricOutput1)
        blockedQueriesMetric.getMetricResponseMetadata(metricInput = blockedQueriesInput2, metricOutput = metricOutput1)
        blockedQueriesMetric.getMetricResponseMetadata(metricInput = blockedQueriesInput3, metricOutput = metricOutput2)
        blockedQueriesMetric.getMetricResponseMetadata(metricInput = blockedQueriesInput4, metricOutput = metricOutput3)
        blockedQueriesMetric.getMetricResponseMetadata(metricInput = blockedQueriesInput3, metricOutput = metricOutput3)
    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        // "main" query defined
        val metricConfig3 = MetricConfig(
            queries = mapOf("main" to "SELECT randomColumn from randomTable"),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )

        // empty "main" query
        val metricConfig4 = MetricConfig(
            queries = mapOf("main" to ""),
            supportsHistorical = false,
            supportsRealTime = true,
            isParameterized = false,
            referenceDoc = "",
            description = ""
        )
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput1,
            metricConfig = metricConfig3)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput2,
            metricConfig = metricConfig3)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput1,
            metricConfig = metricConfig4)
        blockedQueriesMetric.getMetricResult(metricInput = blockedQueriesInput2,
            metricConfig = metricConfig4)
    }
}