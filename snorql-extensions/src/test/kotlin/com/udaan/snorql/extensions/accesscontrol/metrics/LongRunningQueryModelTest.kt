package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.metrics.LongRunningQueriesMetric
import com.udaan.snorql.extensions.performance.models.LongRunningResult
import com.udaan.snorql.extensions.performance.models.LongRunningInput
import com.udaan.snorql.extensions.performance.models.LongRunningQueryDTO
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class LongRunningQueryModelTest {
    companion object {
        val longRunningQueriesMetric = LongRunningQueriesMetric()
    }

    // Index Stat Metric Main Query String
    private val longRunningQueryMetricMainQuery: String? =
        longRunningQueriesMetric.getMetricConfig(LongRunningInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            elapsedTime = "5").metricId).queries["main"]

    // Long Running Queries Inputs
    private val longRunningQueriesInput1 =
        LongRunningInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            elapsedTime = "5")
    private val longRunningQueriesInput2 =
        LongRunningInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            elapsedTime = "5")
    private val longRunningQueriesInput3 =
        LongRunningInput(metricId = "randomMetricId", metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            elapsedTime = "5")
    private val longRunningQueriesInput4 =
        LongRunningInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            elapsedTime = "5")

    // Random Long Running Query
    private val longRunningQuery1 = LongRunningQueryDTO(sessionId = 1234,
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
        openTransactionCount = 1
    )
    private val longRunningQuery2 = LongRunningQueryDTO(sessionId = 4321,
        status = "Waiting",
        blockedBy = 1234,
        waitType = "Blocked",
        waitResource = "Printer",
        waitTime = "48 seconds",
        cpuTime = 66,
        logicalReads = 34,
        reads = null,
        writes = null,
        elapsedTime = "99 seconds",
        queryText = "SELECT randomColumn FROM randomTable",
        storedProc = "Some stored procedure",
        command = "Some random command",
        loginName = "Login Name",
        hostName = "Some Host Name",
        programName = "Some Program name",
        hostProcessId = 345,
        lastRequestEndTime = "78 seconds ago",
        loginTime = "22:45:59",
        openTransactionCount = 1
    )

    // Index Stat Results
    private val longRunningResult1 =
        LongRunningResult(listOf(longRunningQuery1, longRunningQuery2)) // Multiple stats in result
    private val longRunningResult2 = LongRunningResult(listOf(longRunningQuery2))
    private val longRunningResult3 = LongRunningResult(listOf()) // empty result

    // Index Stat Output
    private val metricOutput1 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningResult1, null)
    private val metricOutput2 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningResult2, null)
    private val metricOutput3 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(longRunningQueryMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput1,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput1,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput1,
                metricOutput3))
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput2,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput2,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(longRunningQueriesInput2,
                metricOutput3))
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
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput1,
            metricConfig = metricConfig1)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput2,
            metricConfig = metricConfig1)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput1,
            metricConfig = metricConfig2)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput2,
            metricConfig = metricConfig2)
        longRunningQueriesMetric.getMetricResponseMetadata(metricInput = longRunningQueriesInput3,
            metricOutput = metricOutput1)
        longRunningQueriesMetric.getMetricResponseMetadata(metricInput = longRunningQueriesInput4,
            metricOutput = metricOutput1)
        longRunningQueriesMetric.getMetricResponseMetadata(metricInput = longRunningQueriesInput3,
            metricOutput = metricOutput2)
        longRunningQueriesMetric.getMetricResponseMetadata(metricInput = longRunningQueriesInput4,
            metricOutput = metricOutput2)
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
        val longRunningQueriesInput1 =
            LongRunningInput(metricPeriod = MetricPeriod.HISTORICAL,
                databaseName = "randomDatabaseName",
                elapsedTime = "5")
        val longRunningQueriesInput2 =
            LongRunningInput(metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = "randomDatabaseName",
                elapsedTime = "5")
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput1,
            metricConfig = metricConfig3)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput2,
            metricConfig = metricConfig3)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput1,
            metricConfig = metricConfig4)
        longRunningQueriesMetric.getMetricResult(metricInput = longRunningQueriesInput2,
            metricConfig = metricConfig4)
    }
}