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
import com.udaan.snorql.extensions.storage.models.DbTableDTO
import com.udaan.snorql.extensions.storage.models.DbTableInput
import com.udaan.snorql.extensions.storage.models.DbTableResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class DbTableMetricTest {
    companion object {
        private val dbTableMetric = DbTableMetric()
    }

    // DB Metric Query String
    private val dbTableMetricMainQuery: String? = dbTableMetric.getMetricConfig(
        DbTableInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName"
        ).metricId
    ).queries["main"]

    // Database Table Stats
    private val randomDbTableMetric1 = DbTableDTO(
        tableName = "randomTableName1",
        schemaName = "randomSchemaName1",
        rows = "45",
        totalSpaceKB = "3072",
        totalSpaceMB = "3",
        unusedSpaceKB = "2048",
        unusedSpaceMB = "2",
        usedSpaceKB = "1024",
        usedSpaceMB = "1"
    )
    private val randomDbTableMetric2 = DbTableDTO(
        tableName = "randomTableName2",
        schemaName = "randomSchemaName2",
        rows = "90",
        totalSpaceKB = "0",
        totalSpaceMB = "0",
        unusedSpaceKB = "0",
        unusedSpaceMB = "0",
        usedSpaceKB = "0",
        usedSpaceMB = "0"
    )

    // DbTable Metric Inputs
    private val dbTableMetricInputHistorical1 =
        DbTableInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1")
    private val dbTableMetricInputRealTime1 =
        DbTableInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val dbTableMetricInputHistorical2 =
        DbTableInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val dbTableMetricInputRealTime2 =
        DbTableInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName2")
    private val dbTableMetricInputHistorical3 =
        DbTableInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3")
    private val dbTableMetricInputRealTime3 =
        DbTableInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3")
    private val dbTableMetricInputIncorrectMetricId = DbTableInput(
        metricId = "incorrectId",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName1"
    )
    private val dbTableMetricInputEmptyMetricId =
        DbTableInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1")

    // DbTable Metric Result
    private val dbTableMetricResultMultipleResults = DbTableResult(listOf(randomDbTableMetric1, randomDbTableMetric2))
    private val dbTableMetricResultSingleResult = DbTableResult(listOf(randomDbTableMetric1))
    private val dbTableMetricResultEmptyResult = DbTableResult(listOf()) // empty result

    // DbTable Metric Output
    private val metricOutputMultipleResults =
        MetricOutput<DbTableResult, IMetricRecommendation>(dbTableMetricResultMultipleResults, null)
    private val metricOutputSingleResult =
        MetricOutput<DbTableResult, IMetricRecommendation>(dbTableMetricResultSingleResult, null)
    private val metricOutputEmptyResult =
        MetricOutput<DbTableResult, IMetricRecommendation>(dbTableMetricResultEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(dbTableMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputHistorical1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputRealTime2, metricOutputEmptyResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputRealTime1, metricOutputMultipleResults)
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputRealTime1, metricOutputSingleResult)
        )
        assertEquals(
            expected = expectedOutput1,
            dbTableMetric.getMetricResponseMetadata(dbTableMetricInputRealTime1, metricOutputEmptyResult)
        )
        val metricOutputList = listOf(
            metricOutputMultipleResults,
            metricOutputSingleResult,
            metricOutputEmptyResult
        )
        for (metricInput in listOf(dbTableMetricInputIncorrectMetricId, dbTableMetricInputEmptyMetricId)) {
            for (metricOutput in metricOutputList) {
                try {
                    dbTableMetric.getMetricResponseMetadata(metricInput = metricInput, metricOutput = metricOutput)
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
        val databaseNames = listOf("randomDatabaseName1", "randomDatabaseName2", "randomDatabaseName3")
        databaseNames.forEach { databaseName ->
            whenever(
                SqlMetricManager.queryExecutor.execute<DbTableDTO>(
                    databaseName, // "randomDatabaseName1", // "randomDatabaseName1",
                    "MetricMainQuery",
                    // params = any()// "MetricMainQuery" // "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<DbTableDTO>(randomDbTableMetric1, randomDbTableMetric2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(randomDbTableMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<DbTableDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            dbTableMetricResultMultipleResults,
            dbTableMetric.getMetricResult(dbTableMetricInputRealTime1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbTableMetricResultSingleResult,
            dbTableMetric.getMetricResult(dbTableMetricInputRealTime2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbTableMetricResultEmptyResult,
            dbTableMetric.getMetricResult(dbTableMetricInputRealTime3, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbTableMetricResultMultipleResults,
            dbTableMetric.getMetricResult(
                dbTableMetricInputHistorical1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            dbTableMetricResultSingleResult,
            dbTableMetric.getMetricResult(
                dbTableMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            dbTableMetricResultEmptyResult,
            dbTableMetric.getMetricResult(
                dbTableMetricInputHistorical3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Check for failing test cases throwing SQLMonitoringConfigException
        for (metricInput in listOf(dbTableMetricInputIncorrectMetricId, dbTableMetricInputEmptyMetricId)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutQueries,
                TestHelper.metricConfigWithoutMainQuery
            )) {
                try {
                    dbTableMetric.getMetricResult(metricInput = metricInput, metricConfig = metricConfig)
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