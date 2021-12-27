package com.udaan.snorql.extensions.accesscontrol.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.udaan.snorql.extensions.storage.metrics.DbMetric
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.extensions.storage.models.DbResult
import com.udaan.snorql.extensions.storage.models.DbStorageSize
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers.anyString
import kotlin.test.assertEquals
import kotlin.test.fail

class DbMetricTest {
    companion object {
        private val dbMetric = DbMetric()
    }

    // DB Metric Query String
    private val dbMetricMainQuery: String? = dbMetric.getMetricConfig(
        DbInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName", dbName = "randomDBName"
        ).metricId
    ).queries["main"]
    private val dbMetricDbSizeQuery: String? = dbMetric.getMetricConfig(
        DbInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName", dbName = "randomDBName"
        ).metricId
    ).queries["dbSize"]


    // Database Stats Metrics
    private val dbMetric1 = DbDTO(
        databaseName = "randomDatabaseName1",
        databaseSize = "260MB",
        unallocatedSpace = "70MB",
        reserved = "180MB",
        data = "45MB",
        indexSize = "17MB",
        unused = "44MB"
    )
    private val dbMetric2 = DbDTO(
        databaseName = "randomDatabaseName2",
        databaseSize = "26MB",
        unallocatedSpace = "7MB",
        reserved = "18MB",
        data = "4.5MB",
        indexSize = "1.7MB",
        unused = "4.4MB"
    )
    private val dbStorageMetric1 = DbStorageSize(
        dbTotalSize = 400,
        databaseName = "randomDatabaseName1",
        databaseSize = "400MB",
        unallocatedSpace = "240MB",
        reserved = "420MB",
        data = "300MB",
        indexSize = "2MB",
        unused = "68MB"
    )
    private val dbStorageMetric2 = DbStorageSize(
        dbTotalSize = 200,
        databaseName = "randomDatabaseName2",
        databaseSize = "200MB",
        unallocatedSpace = "40MB",
        reserved = "220MB",
        data = "100MB",
        indexSize = "2MB",
        unused = "6MB"
    )

    // DB Metric Inputs
    private val dbMetricInputHistorical =
        DbInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInputRealTime =
        DbInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInputWithIncorrectMetricId =
        DbInput(
            metricId = "randomIncorrectMetricId", // Incorrect metric ID
            metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1"
        )
    private val dbMetricInputWithEmptyMetricId = DbInput(
        metricId = "", // Empty String metric ID
        metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1"
    )

    // DB Metric Result
    private val dbMetricResultMultipleResults = DbResult(listOf(dbStorageMetric1, dbStorageMetric2))
    private val dbMetricResultSingleResult = DbResult(listOf(dbStorageMetric1))
    private val dbMetricResultEmptyResult = DbResult(listOf()) // empty result

    // DB Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<DbResult, IMetricRecommendation>(dbMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<DbResult, IMetricRecommendation>(dbMetricResultSingleResult, null)
    private val metricOutputEmptyResult = MetricOutput<DbResult, IMetricRecommendation>(dbMetricResultEmptyResult, null)

    // Incorrect Metric Inputs
    private val incorrectMetricInputList =
        listOf<DbInput>(dbMetricInputWithIncorrectMetricId, dbMetricInputWithEmptyMetricId)

    // Incorrect metric configs
    private val incorrectMetricConfigList =
        listOf(
            TestHelper.metricConfigWithoutMainQuery,
            TestHelper.metricConfigWithoutMainAndDbSizeQueries,
            TestHelper.metricConfigWithoutQueries
        )


    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(dbMetricMainQuery, dbMetricDbSizeQuery),
            "referenceDocumentation" to "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-spaceused-transact-sql?view=sql-server-ver15#examples",
            "description" to "Displaying updated space information about a database"
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputHistorical, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputHistorical, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputHistorical, metricOutputEmptyResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime, metricOutputEmptyResult)
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in incorrectMetricInputList) {
            for (metricOutput in metricOutputList) {
                try {
                    dbMetric.getMetricResponseMetadata(metricInput = metricInput, metricOutput = metricOutput)
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricOutput")
                }
            }
        }
    }

    @Test
    fun testExecuteFunction() {
        val mockSqlMetricManager = TestHelper.mockSqlMetricManager<DbDTO>(listOf<DbDTO>(dbMetric1, dbMetric2))
        assertEquals(
            listOf(dbMetric1, dbMetric2),
            mockSqlMetricManager.queryExecutor.execute<DbDTO>(
                databaseName = "randomDatabaseName1",
                query = "MetricMainQuery"
            )
        )
    }

    @Test
    fun testGetMetricResult1() {
//        val mockSqlMetricManager = TestHelper.mockSqlMetricManager<DbDTO>(listOf<DbDTO>())
//        whenever(
//            DbMetric().getMetricResult(
//                dbMetricInputRealTime,
//                TestHelper.metricConfigWithMainAndDbSizeQueries
//            )
//        ).thenReturn(
//            DbResult(
//                mockSqlMetricManager.queryExecutor.execute<DbStorageSize>(
//                    databaseName = "randomDatabaseName1",
//                    query = "MetricMainQuery"
//                )
//            )
//        )
    }

    @Test
    fun testGetMetricResult() {

//        val mockConnectionInstance: Connection = mock()
//        SqlMetricManager.setConnection(mockConnectionInstance)
//        whenever(
//            SqlMetricManager.queryExecutor.execute<DbDTO>(
//                eq(any()),
//                eq(any())
//            )
//        ).thenAnswer {
//            listOf<DbDTO>(dbMetric1, dbMetric2)
//        }
//        whenever(
//            SqlMetricManager.queryExecutor.execute<Int>(
//                eq(any()),
//                eq(any())
//            )
//        ).thenAnswer {
//            listOf<Int>(400, 200)
//        }

        assertEquals(
            dbMetricResultMultipleResults,
            dbMetric.getMetricResult(dbMetricInputRealTime, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )


        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in incorrectMetricInputList) {
            for (metricConfig in incorrectMetricConfigList) {
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