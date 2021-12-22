package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.models.LongRunningInput
import com.udaan.snorql.extensions.storage.metrics.DbMetric
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.extensions.storage.models.DbResult
import com.udaan.snorql.extensions.storage.models.DbStorageSize
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.fail

class DbMetricTest {
    companion object {
        private val dbMetric = DbMetric()
    }

    // DB Metric Query String
    private val dbMetricMainQuery: String? = dbMetric.getMetricConfig(
        DbInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName", dbName = "randomDBName").metricId).queries["main"]
    private val dbMetricDbSizeQuery: String? = dbMetric.getMetricConfig(
        DbInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName", dbName = "randomDBName").metricId).queries["dbSize"]

    // Metric configs
    private val metricConfig1 =
        MetricConfig(queries = mapOf("main" to "dbMetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true)
    private val metricConfig2 =
        MetricConfig(queries = mapOf("notMain" to "dbMetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true)
    private val metricConfig3 =
        MetricConfig(queries = mapOf("main" to "dbMetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true)
    private val metricConfig4 =
        MetricConfig(queries = mapOf("notMain" to "dbMetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true)
    private val metricConfig5 =
        MetricConfig(queries = mapOf(),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = "",
            supportsHistorical = true,
            supportsRealTime = true)


    // Database Stats Metrics
    private val dbMetric1 = DbDTO(databaseName = "randomDatabaseName1",
        databaseSize = "260MB",
        unallocatedSpace = "70MB",
        reserved = "180MB",
        data = "45MB",
        indexSize = "17MB",
        unused = "44MB")
    private val dbMetric2 = DbDTO(databaseName = "randomDatabaseName2",
        databaseSize = "26MB",
        unallocatedSpace = "7MB",
        reserved = "18MB",
        data = "4.5MB",
        indexSize = "1.7MB",
        unused = "4.4MB")
    private val dbStorageMetric1 = DbStorageSize(dbTotalSize = 400,
        databaseName = "randomDatabaseName1",
        databaseSize = "400MB",
        unallocatedSpace = "240MB",
        reserved = "420MB",
        data = "300MB",
        indexSize = "2MB",
        unused = "68MB")
    private val dbStorageMetric2 = DbStorageSize(dbTotalSize = 200,
        databaseName = "randomDatabaseName2",
        databaseSize = "200MB",
        unallocatedSpace = "40MB",
        reserved = "220MB",
        data = "100MB",
        indexSize = "2MB",
        unused = "6MB")

    // DB Metric Inputs
    private val dbMetricInput1 =
        DbInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInput2 =
        DbInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInput3 = DbInput(metricId = "randomIncorrectMetricId", // Incorrect metric ID
        metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInput4 = DbInput(metricId = "", // Empty String metric ID
        metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1")

    // DB Metric Result
    private val dbMetricResult1 = DbResult(listOf(dbStorageMetric1, dbStorageMetric2))
    private val dbMetricResult2 = DbResult(listOf(dbStorageMetric1))
    private val dbMetricResult3 = DbResult(listOf()) // empty result

    // DB Metric Output
    private val metricOutput1 = MetricOutput<DbResult, IMetricRecommendation>(dbMetricResult1, null)
    private val metricOutput2 = MetricOutput<DbResult, IMetricRecommendation>(dbMetricResult2, null)
    private val metricOutput3 = MetricOutput<DbResult, IMetricRecommendation>(dbMetricResult3, null)

//    @Rule
//    public val expectedExceptionRule: ExpectedException = ExpectedException.none()

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(dbMetricMainQuery, dbMetricDbSizeQuery),
            "referenceDocumentation" to "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-spaceused-transact-sql?view=sql-server-ver15#examples",
            "description" to "Displaying updated space information about a database"
        )
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput1, metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput1, metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput1, metricOutput3))
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput2, metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput2, metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInput2, metricOutput3))
    }

    @Test
    fun testSQLMonitoringConfigException() {
        val metricInputList = listOf<DbInput>(dbMetricInput3, dbMetricInput4)
        val metricConfigList =
            listOf<MetricConfig>(metricConfig2, metricConfig4, metricConfig5)
        for (metricInput in metricInputList) {
            for (metricConfig in metricConfigList) {
                try {
                    dbMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }
    }
}