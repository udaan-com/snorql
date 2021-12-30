package com.udaan.snorql.extensions.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.metrics.DbMetric
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.extensions.storage.models.DbResult
import com.udaan.snorql.extensions.storage.models.DbStorageSize
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
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
        databaseSize = "400MB",
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
        unallocatedSpace = "70MB",
        reserved = "180MB",
        data = "45MB",
        indexSize = "17MB",
        unused = "44MB"
    )
    private val dbStorageMetric2 = DbStorageSize(
        dbTotalSize = 200,
        databaseName = "randomDatabaseName2",
        databaseSize = "26MB",
        unallocatedSpace = "7MB",
        reserved = "18MB",
        data = "4.5MB",
        indexSize = "1.7MB",
        unused = "4.4MB"
    )

    // DB Metric Inputs
    private val dbMetricInputHistorical =
        DbInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInputRealTime1 =
        DbInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1", dbName = "randomDbName1")
    private val dbMetricInputRealTime2 =
        DbInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName2", dbName = "randomDbName1")
    private val dbMetricInputRealTime3 =
        DbInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3", dbName = "randomDbName1")
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

    // Correct Metric Inputs
    private val correctMetricInputList =
        listOf<DbInput>(dbMetricInputHistorical, dbMetricInputRealTime1, dbMetricInputRealTime2, dbMetricInputRealTime3)

    // Incorrect metric configs
    private val incorrectMetricConfigList =
        listOf(
            TestHelper.metricConfigWithoutMainQuery,
            TestHelper.metricConfigWithoutMainAndDbSizeQueries,
            TestHelper.metricConfigWithoutQueries,
            TestHelper.metricConfigWithoutDbSizeQuery
        )

    // Correct metric configs
    private val correctMetricConfigList =
        listOf(TestHelper.metricConfigWithMainAndDbSizeQueries, TestHelper.metricConfigWithoutDbSizeQuery)


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
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbMetric.getMetricResponseMetadata(dbMetricInputRealTime1, metricOutputEmptyResult)
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

//    @Test
//    fun testExecuteFunction() {
//        val mockSqlMetricManager = TestHelper.mockSqlMetricManager<DbDTO>(listOf<DbDTO>(dbMetric1, dbMetric2))
//        assertEquals(
//            listOf(dbMetric1, dbMetric2),
//            mockSqlMetricManager.queryExecutor.execute<DbDTO>(
//                databaseName = "randomDatabaseName1",
//                query = "MetricMainQuery"
//            )
//        )
//    }

    @Test
    fun testGetMetricResult() {
        // Check for failing test cases throwing SQLMonitoringConnectionException
        for (metricInput in correctMetricInputList) {
            for (metricConfig in correctMetricConfigList) {
                try {
                    dbMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                } catch (e: SQLMonitoringConnectionException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricConfig")
                }
            }
        }
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val databaseNames = listOf("randomDatabaseName1", "randomDatabaseName2", "randomDatabaseName3")
        databaseNames.forEach { databaseName ->
            whenever(
                dbMetric.executeQuery<DbDTO>(
                    databaseName = databaseName, // "randomDatabaseName1", // "randomDatabaseName1",
                    queryString = "MetricMainQuery",
                    // params = any()// "MetricMainQuery" // "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<DbDTO>(dbMetric1, dbMetric2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(dbMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<DbDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }

            whenever(
                dbMetric.executeQuery<Int>(
                    databaseName = databaseName,
                    queryString = "dbSizeQueryString"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<Int>(400, 200)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(400)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<DbDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            dbMetricResultMultipleResults,
            dbMetric.getMetricResult(dbMetricInputRealTime1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbMetricResultSingleResult,
            dbMetric.getMetricResult(dbMetricInputRealTime2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbMetricResultEmptyResult,
            dbMetric.getMetricResult(dbMetricInputRealTime3, TestHelper.metricConfigWithMainAndDbSizeQueries)
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
        reset(mockConnection)
    }
}