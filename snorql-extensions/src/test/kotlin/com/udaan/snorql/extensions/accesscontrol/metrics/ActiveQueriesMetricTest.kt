package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.metrics.ActiveQueriesMetric
import com.udaan.snorql.extensions.performance.models.ActiveDDLInput
import com.udaan.snorql.extensions.performance.models.ActiveQueryDTO
import com.udaan.snorql.extensions.performance.models.ActiveQueryInput
import com.udaan.snorql.extensions.performance.models.ActiveQueryResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class ActiveQueriesMetricTest {
    companion object {
        private val activeQueriesMetric = ActiveQueriesMetric()
    }

    private val activeQueriesMetricMainQuery: String? = activeQueriesMetric.getMetricConfig(ActiveQueryInput(
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName").metricId).queries["main"]

    // Active Query Input
    private val activeQueriesInput1 =
        ActiveQueryInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val activeQueriesInput2 =
        ActiveQueryInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val activeQueriesInput3 = ActiveQueryInput(metricId = "randomMetricID",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName")
    private val activeQueriesInput4 =
        ActiveQueryInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")

    // Random Active Query
    private val activeQuery1 = ActiveQueryDTO(sessionId = 1234,
        status = "Running",
        blockedBy = 4321,
        waitType = null,
        waitResource = null,
        percentComplete = 45,
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
    private val activeQuery2 = ActiveQueryDTO(sessionId = 4321,
        status = "Waiting",
        blockedBy = 1234,
        waitType = "Blocked",
        waitResource = "Printer",
        percentComplete = 34,
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

    // Active Query Configs
    // "main" query not defined in config
    private val metricConfig1 = MetricConfig(
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // empty queries map (no queries defined in config)
    private val metricConfig2 = MetricConfig(
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    // "main" query defined
    private val metricConfig3 = MetricConfig(
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // empty "main" query
    private val metricConfig4 = MetricConfig(
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // Active Query Results
    private val activeQueryResult1 = ActiveQueryResult(listOf(activeQuery1, activeQuery2))
    private val activeQueryResult2 = ActiveQueryResult(listOf(activeQuery1))
    private val activeQueryResult3 = ActiveQueryResult(listOf()) // No queries in result

    // Active Query Metric Outputs
    private val metricOutput1 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult1, null)
    private val metricOutput2 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult2, null)
    private val metricOutput3 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult3, null)

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput1,
            metricConfig = metricConfig1)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput2,
            metricConfig = metricConfig1)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput1,
            metricConfig = metricConfig2)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput2,
            metricConfig = metricConfig2)
        activeQueriesMetric.getMetricResponseMetadata(metricInput = activeQueriesInput1, metricOutput = metricOutput1)
        activeQueriesMetric.getMetricResponseMetadata(metricInput = activeQueriesInput2, metricOutput = metricOutput1)
        activeQueriesMetric.getMetricResponseMetadata(metricInput = activeQueriesInput3, metricOutput = metricOutput2)
        activeQueriesMetric.getMetricResponseMetadata(metricInput = activeQueriesInput4, metricOutput = metricOutput3)
        activeQueriesMetric.getMetricResponseMetadata(metricInput = activeQueriesInput3, metricOutput = metricOutput3)
    }

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(activeQueriesMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput1, metricOutput1))
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput1, metricOutput2))
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput1, metricOutput3))
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput2, metricOutput1))
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput2, metricOutput2))
        assertEquals(expected = expectedOutput1,
            activeQueriesMetric.getMetricResponseMetadata(activeQueriesInput2, metricOutput3))
    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput1,
            metricConfig = metricConfig3)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput2,
            metricConfig = metricConfig3)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput1,
            metricConfig = metricConfig4)
        activeQueriesMetric.getMetricResult(metricInput = activeQueriesInput2,
            metricConfig = metricConfig4)
    }
}