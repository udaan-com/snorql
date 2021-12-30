/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import com.udaan.snorql.extensions.storage.models.TableSchemaDTO
import com.udaan.snorql.extensions.storage.models.TableSchemaInput
import com.udaan.snorql.extensions.storage.models.TableSchemaResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import junit.framework.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class TableSchemaMetricTest {
    companion object {
        val tableSchemaMetric = TableSchemaMetric()
    }

    // Table Schema Metric Query String
    private val tableSchemaMetricMainQuery: String? = tableSchemaMetric.getMetricConfig(
        TableSchemaInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName1"
        ).metricId
    ).queries["main"]

    // Table Schema Metric Stats
    private val tableSchemaResult1 = TableSchemaDTO(
        columnName = "ColumnName1",
        createdDate = "2021-07-23 13:10:11",
        isRowGuid = "Yes",
        isIdentity = "Yes",
        ordinalPosition = 2,
        columnDefault = "DEFAULT_VALUE",
        isNullable = "true",
        dataType = "varchar",
        characterMaximumLength = 24,
        collationName = "CollationName1"
    )
    private val tableSchemaResult2 = TableSchemaDTO(
        columnName = "ColumnName2",
        createdDate = "2021-07-23 13:10:11",
        isRowGuid = "Yes",
        isIdentity = "Yes",
        ordinalPosition = 2,
        columnDefault = null,
        isNullable = "true",
        dataType = "varchar",
        characterMaximumLength = null,
        collationName = null
    )

    // Table Schema Metric Inputs
    private val tableSchemaMetricInputHistorical1 =
        TableSchemaInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", tableName = "randomTableName1")
    private val tableSchemaMetricInputRealTime1 =
        TableSchemaInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1", tableName = "randomTableName1")
    private val tableSchemaMetricInputHistorical2 =
        TableSchemaInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2", tableName = "randomTableName2")
    private val tableSchemaMetricInputRealTime2 =
        TableSchemaInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName2", tableName = "randomTableName2")
    private val tableSchemaMetricInputHistorical3 =
        TableSchemaInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3", tableName = "randomTableName3")
    private val tableSchemaMetricInputRealTime3 =
        TableSchemaInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3", tableName = "randomTableName3")
    private val tableSchemaMetricInputIncorrectMetricId = TableSchemaInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName1",
        tableName = "randomTableName1"
    )
    private val tableSchemaMetricInputEmptyMetricId =
        TableSchemaInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1", tableName = "")

    // Table Schema Metric Result
    private val tableSchemaMetricResultMultipleResults = TableSchemaResult(listOf(tableSchemaResult1, tableSchemaResult2))
    private val tableSchemaMetricResultSingleResult = TableSchemaResult(listOf(tableSchemaResult1))
    private val tableSchemaMetricResultEmptyResult = TableSchemaResult(listOf()) // empty result

    // Table Schema Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<TableSchemaResult, IMetricRecommendation>(tableSchemaMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<TableSchemaResult, IMetricRecommendation>(tableSchemaMetricResultSingleResult, null)
    private val metricOutputEmptyResult =
        MetricOutput<TableSchemaResult, IMetricRecommendation>(tableSchemaMetricResultEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(tableSchemaMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputHistorical1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputRealTime2, metricOutputEmptyResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputRealTime1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            tableSchemaMetric.getMetricResponseMetadata(tableSchemaMetricInputRealTime1, metricOutputEmptyResult)
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in listOf(tableSchemaMetricInputIncorrectMetricId, tableSchemaMetricInputEmptyMetricId)) {
            for (metricOutput in metricOutputList) {
                try {
                    tableSchemaMetric.getMetricResponseMetadata(metricInput = metricInput, metricOutput = metricOutput)
                    Assert.fail("Test did not throw an Exception: \nMetric Input: $metricInput\nMetric Config: $metricOutput")
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
        val metricInputList = listOf(tableSchemaMetricInputRealTime1, tableSchemaMetricInputRealTime2, tableSchemaMetricInputRealTime3, tableSchemaMetricInputHistorical1, tableSchemaMetricInputHistorical2, tableSchemaMetricInputHistorical3)
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<TableSchemaDTO>(
                    metricInput.databaseName,
                    "MetricMainQuery",
                    mapOf("tableName" to metricInput.tableName)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<TableSchemaDTO>(tableSchemaResult1, tableSchemaResult2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(tableSchemaResult1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<TableSchemaDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            tableSchemaMetricResultMultipleResults,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputRealTime1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableSchemaMetricResultSingleResult,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputRealTime2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableSchemaMetricResultEmptyResult,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputRealTime3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableSchemaMetricResultMultipleResults,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputHistorical1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableSchemaMetricResultSingleResult,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            tableSchemaMetricResultEmptyResult,
            tableSchemaMetric.getMetricResult(
                tableSchemaMetricInputHistorical3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in listOf(tableSchemaMetricInputIncorrectMetricId, tableSchemaMetricInputEmptyMetricId)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutQueries,
                TestHelper.metricConfigWithoutMainQuery
            )) {
                try {
                    tableSchemaMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
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