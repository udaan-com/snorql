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

package com.udaan.snorql.extensions.performance.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.performance.models.LongRunningResult
import com.udaan.snorql.extensions.performance.models.LongRunningInput
import com.udaan.snorql.extensions.performance.models.LongRunningQueryDTO
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class LongRunningQueriesMetricTest {
    companion object {
        val longRunningQueriesMetric = LongRunningQueriesMetric()
    }

    // Index Stat Metric Main Query String
    private val longRunningQueryMetricMainQuery: String? =
        longRunningQueriesMetric.getMetricConfig(
            LongRunningInput(
                metricPeriod = MetricPeriod.HISTORICAL,
                databaseName = "randomDatabaseName",
                elapsedTime = "5"
            ).metricId
        ).queries["main"]

    // Long Running Queries Inputs
    private val longRunningQueriesHistoricalInput1 =
        LongRunningInput(
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName1",
            elapsedTime = "5"
        )
    private val longRunningQueriesRealTimeInput2 =
        LongRunningInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName2",
            elapsedTime = "1"
        )
    private val longRunningQueriesRealTimeInput3 =
        LongRunningInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName3",
            elapsedTime = "0"
        )
    private val longRunningQueriesIncorrectMetricIdInput =
        LongRunningInput(
            metricId = "randomMetricId", metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            elapsedTime = "5"
        )
    private val longRunningQueriesEmptyMetricIdInput =
        LongRunningInput(
            metricId = "", metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName",
            elapsedTime = "5"
        )

    // Random Long Running Query
    private val longRunningQuery1 = LongRunningQueryDTO(
        sessionId = 1234,
        status = "Running",
        blockedBy = 4321,
        waitType = null,
        waitResource = null,
        waitTime = "48 seconds",
        cpuTime = 66,
        logicalReads = 34,
        reads = 45,
        writes = 55,
        elapsedTime = "34 seconds",
        queryText = "SELECT randomColumn FROM randomTable",
        storedProc = "Some stored procedure",
        command = "Some random command",
        loginName = "Login Name",
        hostName = "Some Host Name",
        programName = "Some Program name",
        hostProcessId = 345,
        lastRequestEndTime = "78 seconds ago",
        loginTime = "22:45:59",
        openTransactionCount = 1
    )
    private val longRunningQuery2 = LongRunningQueryDTO(
        sessionId = 4321,
        status = "Waiting",
        blockedBy = 1234,
        waitType = "Blocked",
        waitResource = "Printer",
        waitTime = "48 seconds",
        cpuTime = 66,
        logicalReads = 34,
        reads = null,
        writes = null,
        elapsedTime = "99 seconds",
        queryText = "SELECT randomColumn FROM randomTable",
        storedProc = "Some stored procedure",
        command = "Some random command",
        loginName = "Login Name",
        hostName = "Some Host Name",
        programName = "Some Program name",
        hostProcessId = 345,
        lastRequestEndTime = "78 seconds ago",
        loginTime = "22:45:59",
        openTransactionCount = 1
    )

    // Index Stat Results
    private val longRunningMultipleResult =
        LongRunningResult(listOf(longRunningQuery1, longRunningQuery2)) // Multiple stats in result
    private val longRunningSingleResult = LongRunningResult(listOf(longRunningQuery2))
    private val longRunningEmptyResult = LongRunningResult(listOf()) // empty result

    // Index Stat Output
    private val metricOutput1 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningMultipleResult, null)
    private val metricOutput2 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningSingleResult, null)
    private val metricOutput3 = MetricOutput<LongRunningResult, IMetricRecommendation>(longRunningEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(longRunningQueryMetricMainQuery),
            "referenceDocumentation" to listOf<String>(),
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesHistoricalInput1,
                metricOutput1
            )
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesHistoricalInput1,
                metricOutput2
            )
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesHistoricalInput1,
                metricOutput3
            )
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesRealTimeInput2,
                metricOutput1
            )
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesRealTimeInput2,
                metricOutput2
            )
        )
        assertEquals(
            expected = expectedOutput1,
            longRunningQueriesMetric.getMetricResponseMetadata(
                longRunningQueriesRealTimeInput2,
                metricOutput3
            )
        )

        for (metricInput in listOf(longRunningQueriesIncorrectMetricIdInput, longRunningQueriesEmptyMetricIdInput)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2)) {
                try {
                    longRunningQueriesMetric.getMetricResponseMetadata(metricInput, metricOutput)
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
            longRunningQueriesHistoricalInput1, longRunningQueriesRealTimeInput2, longRunningQueriesRealTimeInput3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<LongRunningQueryDTO>(
                    databaseName = metricInput.databaseName,
                    query = "MetricMainQuery",
                    params = mapOf("elapsedTimeParam" to metricInput.elapsedTime)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<LongRunningQueryDTO>(longRunningQuery1, longRunningQuery2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(longRunningQuery2)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<LongRunningQueryDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            longRunningMultipleResult,
            longRunningQueriesMetric.getMetricResult(
                longRunningQueriesHistoricalInput1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            longRunningSingleResult,
            longRunningQueriesMetric.getMetricResult(
                longRunningQueriesRealTimeInput2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            longRunningEmptyResult,
            longRunningQueriesMetric.getMetricResult(
                longRunningQueriesRealTimeInput3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(longRunningQueriesHistoricalInput1, longRunningQueriesRealTimeInput2)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainQuery,
                TestHelper.metricConfigWithoutQueries
            )) {
                try {
                    longRunningQueriesMetric.getMetricResult(metricInput, metricConfig)
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