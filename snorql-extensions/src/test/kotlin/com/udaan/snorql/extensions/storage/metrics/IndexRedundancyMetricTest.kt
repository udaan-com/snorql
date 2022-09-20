package com.udaan.snorql.extensions.storage.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyDTO
import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyInput
import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyResult
import com.udaan.snorql.extensions.storage.models.RedundantReasonDTO
import com.udaan.snorql.extensions.storage.models.RedundantReasons
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

class IndexRedundancyMetricTest {

    companion object {
        val indexRedundancyMetric = DbIndexRedundancyMetric()
    }

    // Get the main query string
    private val indexRedundancyMainQuery: String = indexRedundancyMetric.getMetricConfig(
        DbIndexRedundancyInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName").metricId
    ).queries["main"] ?: throw Exception("Index Redundancy Metric Config not found")

    // Index Redundancy Metric Inputs
    private val indexRedundancyInput1 =
        DbIndexRedundancyInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase1")
    private val indexRedundancyInput2 =
        DbIndexRedundancyInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabase2",
            secondaryDatabaseNames = listOf()
        )
    private val indexRedundancyInput3 =
        DbIndexRedundancyInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase3")
    private val indexRedundancyInput4 =
        DbIndexRedundancyInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase4")
    private val incorrectMetricIdInput = DbIndexRedundancyInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName"
    )
    private val emptyMetricIdInput = DbIndexRedundancyInput(
        metricId = "",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName"
    )

    // Making some DTOs for a common table
    private val indexRedundancyDTO1 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 0,
        indexName = null,
        indexType = "CLUSTERED",
        indexUsage = 500,
        indexUpdates = 1100,
        indexColumnNrs = "1 2 3 4 5",
        indexColumnNames = "col1 col2 col3 col4 col5",
        includeColumnNrs = "6 7",
        includeColumnNames = "col6 col7",
        indexSizeInKb = 100000,
        isUnique = false
    )
    private val indexRedundancyDTO2 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 2,
        indexName = "indexName2",
        indexType = "CLUSTERED",
        indexUsage = 500,
        indexUpdates = 1100,
        indexColumnNrs = "1 2 3",
        indexColumnNames = "col1 col2 col3",
        includeColumnNrs = "6 7",
        includeColumnNames = "col6 col7",
        indexSizeInKb = 100000,
        isUnique = false
    )
    private val indexRedundancyDTO3 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 3,
        indexName = "indexName3",
        indexType = "CLUSTERED",
        indexUsage = 300,
        indexUpdates = 1100,
        indexColumnNrs = "1 2",
        indexColumnNames = "col1 col2",
        includeColumnNrs = "6 7",
        includeColumnNames = "col6 col7",
        indexSizeInKb = 10000,
        isUnique = false
    )
    private val indexRedundancyDTO4 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 4,
        indexName = "indexName4",
        indexType = "CLUSTERED",
        indexUsage = 1000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6",
        includeColumnNames = "col6",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyDTO5 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 5,
        indexName = "indexName5",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6",
        includeColumnNames = "col6",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyDTO6 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 6,
        indexName = "indexName6",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6",
        includeColumnNames = "col6",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyDTO7 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 7,
        indexName = "indexName7",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6 7",
        includeColumnNames = "col6 col7",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyResultDTO7 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 7,
        indexName = "indexName7",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6 7",
        includeColumnNames = "col6 col7",
        indexSizeInKb = 10000,
        isUnique = false,
        reason = RedundantReasonDTO(
            message = "Index is SIMILAR to ${indexRedundancyDTO6.indexName}. Include missing Include columns in " +
                    "${indexRedundancyDTO6.indexName}, post which this index can be deleted.",
            type = RedundantReasons.SIMILAR,
            servingIndex = indexRedundancyDTO6.indexName
        )
    )

    private val indexRedundancyDTO8 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 8,
        indexName = "indexName8",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6 7 8 9 10",
        includeColumnNames = "col6 col7 col8 col9 col10",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyDTO9 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 9,
        indexName = "indexName9",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1",
        indexColumnNames = "col1",
        includeColumnNrs = "",
        includeColumnNames = "",
        indexSizeInKb = 10000,
        isUnique = false
    )

    private val indexRedundancyResultDTO9 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 9,
        indexName = "indexName9",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1",
        indexColumnNames = "col1",
        includeColumnNrs = "",
        includeColumnNames = "",
        indexSizeInKb = 10000,
        isUnique = false,
        reason = RedundantReasonDTO(
            message = "${indexRedundancyDTO8.indexName} OVERLAPS this index",
            type = RedundantReasons.OVERLAPPING,
            servingIndex = indexRedundancyDTO8.indexName
        )
    )

    private val indexRedundancyResultDTO5 = DbIndexRedundancyDTO(
        tableObjectId = 1,
        tableName = "tableName",
        indexId = 5,
        indexName = "indexName5",
        indexType = "CLUSTERED",
        indexUsage = 5000,
        indexUpdates = 1100,
        indexColumnNrs = "1 3 4",
        indexColumnNames = "col1 col3 col4",
        includeColumnNrs = "6",
        includeColumnNames = "col6",
        indexSizeInKb = 10000,
        isUnique = false,
        reason = RedundantReasonDTO(
            message = "DUPLICATE of ${indexRedundancyDTO4.indexName}",
            type = RedundantReasons.DUPLICATE,
            servingIndex = indexRedundancyDTO4.indexName
        )
    )

    // Results
    private val indexRedundancyResult3 =
        DbIndexRedundancyResult(queryList = listOf(indexRedundancyDTO4, indexRedundancyResultDTO5))
    private val indexRedundancyResult4 = DbIndexRedundancyResult(
        queryList = listOf(indexRedundancyDTO6, indexRedundancyResultDTO7)
    )
    private val indexRedundancyResult5 = DbIndexRedundancyResult(
        queryList = listOf(indexRedundancyDTO8, indexRedundancyResultDTO9)
    )

    private val metricInputList =
        listOf(indexRedundancyInput1, indexRedundancyInput2, indexRedundancyInput3, indexRedundancyInput4)

    @Test
    fun testDirectOverlappingIndexesWithHeapIndex() {
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val metricInput = indexRedundancyInput1
        whenever(
            SqlMetricManager.queryExecutor.execute<DbIndexRedundancyDTO>(
                databaseName = metricInput.databaseName,
                query = "MetricMainQuery"
            )
        ).thenAnswer {
            listOf<DbIndexRedundancyDTO>(indexRedundancyDTO1, indexRedundancyDTO2, indexRedundancyDTO3)
        }

        val indexRedundancyResultDTO3 = indexRedundancyDTO3.copy(
            reason = RedundantReasonDTO(
                message = "${indexRedundancyDTO2.indexName} OVERLAPS this index",
                type = RedundantReasons.OVERLAPPING, servingIndex = indexRedundancyDTO2.indexName
            )
        )

        val indexRedundancyResult1 =
            DbIndexRedundancyResult(queryList = listOf(indexRedundancyDTO2, indexRedundancyResultDTO3))

        assertEquals(
            indexRedundancyResult1,
            indexRedundancyMetric.getMetricResult(
                indexRedundancyInput1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
    }

    @Test
    fun testGetMetricResult() {
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<DbIndexRedundancyDTO>(
                    databaseName = metricInput.databaseName,
                    query = "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabase1") -> {
                        listOf<DbIndexRedundancyDTO>(indexRedundancyDTO1, indexRedundancyDTO2, indexRedundancyDTO3)
                    }
                    (database == "randomDatabase2") -> {
                        listOf<DbIndexRedundancyDTO>(indexRedundancyDTO1, indexRedundancyDTO4, indexRedundancyDTO5)
                    }
                    (database == "randomDatabase3") -> {
                        listOf(indexRedundancyDTO6, indexRedundancyDTO7)
                    }
                    (database == "randomDatabase4") -> {
                        listOf<DbIndexRedundancyDTO>(indexRedundancyDTO8, indexRedundancyDTO9)
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            indexRedundancyResult3,
            indexRedundancyMetric.getMetricResult(
                indexRedundancyInput2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        assertEquals(
            indexRedundancyResult4,
            indexRedundancyMetric.getMetricResult(
                indexRedundancyInput3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        assertEquals(
            indexRedundancyResult5,
            indexRedundancyMetric.getMetricResult(
                indexRedundancyInput4,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

    }
}