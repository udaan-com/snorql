package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.storage.metrics.DbIndexMetric
import com.udaan.snorql.extensions.storage.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import kotlin.test.assertEquals
import org.junit.Test

class DbIndexMetricTest {
    companion object {
        val dbIndexMetric = DbIndexMetric()
    }

    // DB Index Metric Main Query
    private val dbIndexMetricMainQuery: String? =
        dbIndexMetric.getMetricConfig(DbIndexInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName").metricId).queries["main"]

    // DB Index Metric Inputs
    private val dbIndexInput1 =
        DbIndexInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val dbIndexInput2 =
        DbIndexInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val dbIndexInput3 =
        DbIndexInput(metricId = "incorrectMetricID", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val dbIndexInput4 =
        DbIndexInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")

    private val metricConfig1 = MetricConfig(
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig2 = MetricConfig(
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig3 = MetricConfig(
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig4 = MetricConfig(
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // DB Index Metrics
    private val dbIndexMetric1 = DbIndexDTO(rows = "344 rows",
        indexName = "randomIndexName",
        tableName = "randomTableName",
        totalSpaceMB = "566MB",
        unusedSpaceMB = "120MB",
        usedSpaceMB = "334MB")
    private val dbIndexMetric2 = DbIndexDTO(rows = "344 rows",
        indexName = null,
        tableName = "randomTableName2",
        totalSpaceMB = "56MB",
        unusedSpaceMB = "22MB",
        usedSpaceMB = "34MB")

    // DB Index Results
    private val dbIndexResult1 =
        DbIndexResult(listOf(dbIndexMetric1, dbIndexMetric2))
    private val dbIndexResult2 = DbIndexResult(listOf(dbIndexMetric1))
    private val dbIndexResult3 = DbIndexResult(listOf())

    // Metric outputs
    private val metricOutput1 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexResult1, null)
    private val metricOutput2 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexResult2, null)
    private val metricOutput3 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(dbIndexMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput1,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput1,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput1,
                metricOutput3))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput2,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput2,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexInput2,
                metricOutput3))
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        dbIndexMetric.getMetricResult(dbIndexInput1, metricConfig1)
        dbIndexMetric.getMetricResult(dbIndexInput2, metricConfig1)
        dbIndexMetric.getMetricResult(dbIndexInput1, metricConfig2)
        dbIndexMetric.getMetricResult(dbIndexInput2, metricConfig2)
        dbIndexMetric.getMetricResponseMetadata(metricInput = dbIndexInput3, metricOutput = metricOutput1)
        dbIndexMetric.getMetricResponseMetadata(metricInput = dbIndexInput4, metricOutput = metricOutput1)
        dbIndexMetric.getMetricResponseMetadata(metricInput = dbIndexInput3, metricOutput = metricOutput2)
        dbIndexMetric.getMetricResponseMetadata(metricInput = dbIndexInput4, metricOutput = metricOutput2)
    }

    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        dbIndexMetric.getMetricResult(dbIndexInput1, metricConfig3)
        dbIndexMetric.getMetricResult(dbIndexInput2, metricConfig3)
        dbIndexMetric.getMetricResult(dbIndexInput1, metricConfig4)
        dbIndexMetric.getMetricResult(dbIndexInput2, metricConfig4)
    }
}