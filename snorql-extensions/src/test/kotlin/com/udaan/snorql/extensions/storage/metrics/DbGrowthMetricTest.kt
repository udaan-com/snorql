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
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.storage.models.DbGrowthDTO
import com.udaan.snorql.extensions.storage.models.DbGrowthInput
import com.udaan.snorql.extensions.storage.models.DbGrowthResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.fail

class DbGrowthMetricTest {
    companion object {
        val dbGrowthMetric = DbGrowthMetric()
    }

    // DB Growth Metric Main Query
    private val dbGrowthMetricMainQuery: String? =
        dbGrowthMetric.getMetricConfig(
            DbGrowthInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = "randomDatabaseName", dbNameForGrowth = "randomDBName"
            ).metricId
        ).queries["main"]

    // DB Growth Metric Inputs
    private val dbGrowthHistoricalInput1 =
        DbGrowthInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            dbNameForGrowth = "randomDBName1"
        )
    private val dbGrowthRealTimeInput2 =
        DbGrowthInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName2",
            dbNameForGrowth = "randomDBName2"
        )
    private val dbGrowthRealTimeInput3 =
        DbGrowthInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName3",
            dbNameForGrowth = "randomDBName3"
        )
    private val dbGrowthIncorrectMetricIdInput =
        DbGrowthInput(
            metricId = "incorrectMetricID", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName"
        )
    private val dbGrowthEmptyMetricIdInput =
        DbGrowthInput(
            metricId = "", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            dbNameForGrowth = "randomDBName"
        )

    // DB Growth Metrics
    private val dbGrowthMetric1 = DbGrowthDTO(startTime = "22:08:19", endTime = "22:10:44", storageInMegabytes = "84MB")
    private val dbGrowthMetric2 = DbGrowthDTO(startTime = "20:08:19", endTime = "22:10:43", storageInMegabytes = "8MB")
    private val dbGrowthMetric3 = DbGrowthDTO(startTime = "22:08:19", endTime = "22:11:43", storageInMegabytes = "89MB")
    private val dbGrowthMetric4 = DbGrowthDTO(startTime = "21:08:19", endTime = "23:10:48", storageInMegabytes = "99MB")

    // DB Growth Results
    private val dbGrowthMultipleResult =
        DbGrowthResult(listOf(dbGrowthMetric1, dbGrowthMetric2, dbGrowthMetric3, dbGrowthMetric4))
    private val dbGrowthSingleResult = DbGrowthResult(listOf(dbGrowthMetric1))
    private val dbGrowthEmptyResult = DbGrowthResult(listOf())

    // Metric outputs
    private val metricOutput1 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthMultipleResult, null)
    private val metricOutput2 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthSingleResult, null)
    private val metricOutput3 = MetricOutput<DbGrowthResult, IMetricRecommendation>(dbGrowthEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(dbGrowthMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthHistoricalInput1,
                metricOutput1
            )
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthHistoricalInput1,
                metricOutput2
            )
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthHistoricalInput1,
                metricOutput3
            )
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthRealTimeInput2,
                metricOutput1
            )
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthRealTimeInput2,
                metricOutput2
            )
        )
        assertEquals(
            expected = expectedOutput1,
            dbGrowthMetric.getMetricResponseMetadata(
                dbGrowthRealTimeInput2,
                metricOutput3
            )
        )

        for (metricInput in listOf(dbGrowthIncorrectMetricIdInput, dbGrowthEmptyMetricIdInput)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2)) {
                try {
                    dbGrowthMetric.getMetricResponseMetadata(metricInput, metricOutput)
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
            dbGrowthHistoricalInput1, dbGrowthRealTimeInput2, dbGrowthRealTimeInput3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<DbGrowthDTO>(
                    metricInput.databaseName,
                    "MetricMainQuery",
                    mapOf("databaseName" to metricInput.dbNameForGrowth)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<DbGrowthDTO>(dbGrowthMetric1, dbGrowthMetric2, dbGrowthMetric3, dbGrowthMetric4)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf<DbGrowthDTO>(dbGrowthMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<DbGrowthDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            dbGrowthMultipleResult,
            dbGrowthMetric.getMetricResult(dbGrowthHistoricalInput1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbGrowthSingleResult,
            dbGrowthMetric.getMetricResult(dbGrowthRealTimeInput2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            dbGrowthEmptyResult,
            dbGrowthMetric.getMetricResult(dbGrowthRealTimeInput3, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(dbGrowthIncorrectMetricIdInput, dbGrowthEmptyMetricIdInput)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainQuery,
                TestHelper.metricConfigWithoutQueries
            )) {
                try {
                    dbGrowthMetric.getMetricResult(metricInput, metricConfig)
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