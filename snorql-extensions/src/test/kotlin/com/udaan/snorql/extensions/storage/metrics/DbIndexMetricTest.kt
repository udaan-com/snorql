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
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import kotlin.test.assertEquals
import org.junit.Test
import kotlin.test.fail

class DbIndexMetricTest {
    companion object {
        val dbIndexMetric = DbIndexMetric()
    }

    // DB Index Metric Main Query
    private val dbIndexMetricMainQuery: String? =
        dbIndexMetric.getMetricConfig(DbIndexInput(metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName").metricId).queries["main"]

    // DB Index Metric Inputs
    private val dbIndexRealTimeInput1 =
        DbIndexInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val dbIndexHistoricalInput2 =
        DbIndexInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val dbIndexHistoricalInput3 =
        DbIndexInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3")
    private val dbIndexIncorrectMetricIdInput =
        DbIndexInput(metricId = "incorrectMetricID", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")
    private val dbIndexEmptyMetricIdInput =
        DbIndexInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName")

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
    private val dbIndexMultipleResult =
        DbIndexResult(listOf(dbIndexMetric1, dbIndexMetric2))
    private val dbIndexSingleResult = DbIndexResult(listOf(dbIndexMetric1))
    private val dbIndexEmptyResult = DbIndexResult(listOf())

    // Metric outputs
    private val metricOutput1 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexMultipleResult, null)
    private val metricOutput2 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexSingleResult, null)
    private val metricOutput3 = MetricOutput<DbIndexResult, IMetricRecommendation>(dbIndexEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(dbIndexMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexRealTimeInput1,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexRealTimeInput1,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexRealTimeInput1,
                metricOutput3))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexHistoricalInput2,
                metricOutput1))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexHistoricalInput2,
                metricOutput2))
        assertEquals(expected = expectedOutput1,
            dbIndexMetric.getMetricResponseMetadata(dbIndexHistoricalInput2,
                metricOutput3))

        for (metricInput in listOf(dbIndexIncorrectMetricIdInput, dbIndexEmptyMetricIdInput)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2)) {
                try {
                    dbIndexMetric.getMetricResponseMetadata(metricInput, metricOutput)
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricOutput = $metricOutput")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Incorrect exception: $e \n thrown for metricInput = $metricInput \nmetricOutput = $metricOutput")
                }
            }
        }
    }

    @Test
    fun testGetMetricResult() {
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val metricInputList = listOf(
            dbIndexRealTimeInput1, dbIndexHistoricalInput2, dbIndexHistoricalInput3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<DbIndexDTO>(
                    metricInput.databaseName,
                    "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<DbIndexDTO>(dbIndexMetric1, dbIndexMetric2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf<DbIndexDTO>(dbIndexMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<DbIndexDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            dbIndexMultipleResult,
            dbIndexMetric.getMetricResult(dbIndexRealTimeInput1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbIndexSingleResult,
            dbIndexMetric.getMetricResult(dbIndexHistoricalInput2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbIndexEmptyResult,
            dbIndexMetric.getMetricResult(dbIndexHistoricalInput3, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(dbIndexRealTimeInput1, dbIndexHistoricalInput2)) {
            for (metricConfig in listOf(TestHelper.metricConfigWithoutMainQuery, TestHelper.metricConfigWithoutQueries)) {
                try {
                    dbIndexMetric.getMetricResult(metricInput, metricConfig)
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricConfig")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Incorrect exception: $e \n thrown for metricInput = $metricInput \nmetricConfig = $metricConfig")
                }
            }
        }
    }
}