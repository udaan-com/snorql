package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.models.BlockedQueriesResult
import com.udaan.snorql.extensions.performance.models.IndexStatInput
import com.udaan.snorql.extensions.storage.metrics.DbGrowthMetric
import com.udaan.snorql.extensions.storage.models.DbGrowthDTO
import com.udaan.snorql.extensions.storage.models.DbGrowthInput
import com.udaan.snorql.extensions.storage.models.DbGrowthResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class DbGrowthMetricTest {
    companion object {
        val dbGrowthMetric = DbGrowthMetric()
    }

    // DB Growth Metric Main Query
    private val dbGrowthMetricMainQuery: String? =
        dbGrowthMetric.getMetricConfig(DbGrowthInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName", dbNameForGrowth = "randomDBName").metricId).queries["main"]

    // DB Growth Metric Inputs
    private val dbGrowthInput1 =
        DbGrowthInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName")
    private val dbGrowthInput2 =
        DbGrowthInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName")
    private val dbGrowthInput3 =
        DbGrowthInput(metricId = "incorrectMetricID", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName")
    private val dbGrowthInput4 =
        DbGrowthInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName")

    // Configurations
    private val metricConfig1 = MetricConfig(     // "main" query not defined in config
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig2 = MetricConfig(     // empty queries map (no queries defined in config)
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig3 = MetricConfig(              // "main" query defined
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig4 = MetricConfig(               // empty "main" query
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // DB Growth Metrics
    private val dbGrowthMetric1 = DbGrowthDTO(startTime = "22:08:19", endTime = "22:10:44", storageInMegabytes = "84MB")
    private val dbGrowthMetric2 = DbGrowthDTO(startTime = "20:08:19", endTime = "22:10:43", storageInMegabytes = "8MB")
    private val dbGrowthMetric3 = DbGrowthDTO(startTime = "22:08:19", endTime = "22:11:43", storageInMegabytes = "89MB")
    private val dbGrowthMetric4 = DbGrowthDTO(startTime = "21:08:19", endTime = "23:10:48", storageInMegabytes = "99MB")

    // DB Growth Results
    private val dbGrowthResult1 =
        DbGrowthResult(listOf(dbGrowthMetric1, dbGrowthMetric2, dbGrowthMetric3, dbGrowthMetric4))
    private val dbGrowthResult2 = DbGrowthResult(listOf(dbGrowthMetric1))
    private val dbGrowthResult3 = DbGrowthResult(listOf())

    // Metric outputs
    private val metricOutput1 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthResult1, null)
    private val metricOutput2 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthResult2, null)
    private val metricOutput3 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(dbGrowthMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput1,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput1,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput1,
                metricOutput3))
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput2,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput2,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(dbGrowthInput2,
                metricOutput3))
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput1, metricConfig = metricConfig1)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput2, metricConfig = metricConfig1)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput1, metricConfig = metricConfig2)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput2, metricConfig = metricConfig2)
        dbGrowthMetric.getMetricResponseMetadata(metricInput = dbGrowthInput3, metricOutput = metricOutput1)
        dbGrowthMetric.getMetricResponseMetadata(metricInput = dbGrowthInput4, metricOutput = metricOutput1)
        dbGrowthMetric.getMetricResponseMetadata(metricInput = dbGrowthInput3, metricOutput = metricOutput2)
        dbGrowthMetric.getMetricResponseMetadata(metricInput = dbGrowthInput4, metricOutput = metricOutput2)
    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput1, metricConfig = metricConfig3)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput2, metricConfig = metricConfig3)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput1, metricConfig = metricConfig4)
        dbGrowthMetric.getMetricResult(metricInput = dbGrowthInput2, metricConfig = metricConfig4)
    }
}