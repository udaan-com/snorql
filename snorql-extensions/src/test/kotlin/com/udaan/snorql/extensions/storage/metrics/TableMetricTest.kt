/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.extensions.storage.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.models.TableDTO
import com.udaan.snorql.extensions.storage.models.TableInput
import com.udaan.snorql.extensions.storage.models.TableResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TableMetricTest {
    companion object {
        private val tableMetric = TableMetric()
    }

    // Table Metric Query String
    private val tableMetricMainQuery: String? = tableMetric.getMetricConfig(
        TableInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName1"
        ).metricId
    ).queries["main"]

    // Table Metric Stats
    private val tableResult1 = TableDTO(
        name = "randomTable1",
        rows = "220",
        reserved = "45MB",
        data = "22MB",
        indexSize = "4MB",
        unused = "12MB"
    )
    private val tableResult2 = TableDTO(
        name = "randomTable2",
        rows = "22.0",
        reserved = "4.5MB",
        data = "2.2MB",
        indexSize = "0.4MB",
        unused = "1.2MB"
    )

    // Table Metric Inputs
    private val tableMetricInputHistorical1 =
        TableInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            tableName = "randomTableName1"
        )
    private val tableMetricInputRealTime1 =
        TableInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName1",
            tableName = "randomTableName1"
        )
    private val tableMetricInputHistorical2 =
        TableInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName2",
            tableName = "randomTableName2"
        )
    private val tableMetricInputRealTime2 =
        TableInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName2",
            tableName = "randomTableName2"
        )
    private val tableMetricInputHistorical3 =
        TableInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName3",
            tableName = "randomTableName3"
        )
    private val tableMetricInputRealTime3 =
        TableInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName3",
            tableName = "randomTableName3"
        )
    private val tableMetricInputIncorrectMetricId = TableInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName1",
        tableName = "randomTableName1"
    )
    private val tableMetricInputEmptyMetricId =
        TableInput(
            metricId = "",
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            tableName = ""
        )

    // Table Metric Result
    private val tableMetricResultMultipleResults = TableResult(listOf(tableResult1, tableResult2))
    private val tableMetricResultSingleResult = TableResult(listOf(tableResult1))
    private val tableMetricResultEmptyResult = TableResult(listOf()) // empty result

    // Table Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<TableResult, IMetricRecommendation>(tableMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<TableResult, IMetricRecommendation>(tableMetricResultSingleResult, null)
    private val metricOutputEmptyResult =
        MetricOutput<TableResult, IMetricRecommendation>(tableMetricResultEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(tableMetricMainQuery),
            "referenceDocumentation" to listOf<String>("https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-spaceused-transact-sql?view=sql-server-ver15#examples"),
            "description" to "Displaying disk space information about a table"
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputHistorical1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputRealTime2, metricOutputEmptyResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputRealTime1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableMetric.getMetricResponseMetadata(tableMetricInputRealTime1, metricOutputEmptyResult)
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in listOf(tableMetricInputIncorrectMetricId, tableMetricInputEmptyMetricId)) {
            for (metricOutput in metricOutputList) {
                try {
                    tableMetric.getMetricResponseMetadata(metricInput = metricInput, metricOutput = metricOutput)
                    fail("Test did not throw an Exception: \nMetric Input: $metricInput\nMetric Config: $metricOutput")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Test failing with Exception: $e\nMetric Input: $metricInput\nMetric Config: $metricOutput")
                }
            }
        }
    }

    @Test
    fun testGetMetricResult() {
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val metricInputList = listOf(
            tableMetricInputRealTime1,
            tableMetricInputRealTime2,
            tableMetricInputRealTime3,
            tableMetricInputHistorical1,
            tableMetricInputHistorical2,
            tableMetricInputHistorical3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<TableDTO>(
                    metricInput.databaseName,
                    "MetricMainQuery",
                    mapOf("tableName" to metricInput.tableName)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<TableDTO>(tableResult1, tableResult2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(tableResult1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<TableDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            tableMetricResultMultipleResults,
            tableMetric.getMetricResult(
                tableMetricInputRealTime1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableMetricResultSingleResult,
            tableMetric.getMetricResult(
                tableMetricInputRealTime2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableMetricResultEmptyResult,
            tableMetric.getMetricResult(
                tableMetricInputRealTime3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableMetricResultMultipleResults,
            tableMetric.getMetricResult(
                tableMetricInputHistorical1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableMetricResultSingleResult,
            tableMetric.getMetricResult(
                tableMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableMetricResultEmptyResult,
            tableMetric.getMetricResult(
                tableMetricInputHistorical3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in listOf(tableMetricInputIncorrectMetricId, tableMetricInputEmptyMetricId)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutQueries,
                TestHelper.metricConfigWithoutMainQuery
            )) {
                try {
                    tableMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
                    fail("Test did not throw an Exception: \nMetric Input: $metricInput\nMetric Config: $metricConfig")
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