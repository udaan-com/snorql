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

package com.udaan.snorql.extensions.performance.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.performance.models.ActiveQueryDTO
import com.udaan.snorql.extensions.performance.models.ActiveQueryInput
import com.udaan.snorql.extensions.performance.models.ActiveQueryResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.fail


class ActiveQueriesMetricTest {
    companion object {
        private val activeQueriesMetric = ActiveQueriesMetric()
    }

    private val activeQueriesMetricMainQuery: String? = activeQueriesMetric.getMetricConfig(
        ActiveQueryInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName"
        ).metricId
    ).queries["main"]

    // Active Query Input
    private val activeQueriesInput1 =
        ActiveQueryInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1")
    private val activeQueriesInput2 =
        ActiveQueryInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName2")
    private val activeQueriesInput3 = ActiveQueryInput(
        metricId = "randomMetricID",
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName"
    )
    private val activeQueriesInput4 =
        ActiveQueryInput(metricId = "", metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName")
    private val activeQueriesInput5 =
        ActiveQueryInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3")

    // Random Active Query
    private val activeQuery1 = ActiveQueryDTO(
        sessionId = 1234,
        status = "Running",
        blockedBy = 4321,
        waitType = null,
        waitResource = null,
        percentComplete = 45,
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
    private val activeQuery2 = ActiveQueryDTO(
        sessionId = 4321,
        status = "Waiting",
        blockedBy = 1234,
        waitType = "Blocked",
        waitResource = "Printer",
        percentComplete = 34,
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

    // Active Query Configs
    // "main" query not defined in config
    private val metricConfig1 = MetricConfig(
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // empty queries map (no queries defined in config)
    private val metricConfig2 = MetricConfig(
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // "main" query defined
    private val metricConfig3 = MetricConfig(
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // empty "main" query
    private val metricConfig4 = MetricConfig(
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // Active Query Results
    private val activeQueryResult1 = ActiveQueryResult(listOf(activeQuery1, activeQuery2))
    private val activeQueryResult2 = ActiveQueryResult(listOf(activeQuery1))
    private val activeQueryResult3 = ActiveQueryResult(listOf()) // No queries in result

    // Active Query Metric Outputs
    private val metricOutput1 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult1, null)
    private val metricOutput2 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult2, null)
    private val metricOutput3 = MetricOutput<ActiveQueryResult, IMetricRecommendation>(activeQueryResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(activeQueriesMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )

        for (metricInput in listOf(activeQueriesInput1, activeQueriesInput2)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2, metricOutput3)) {
                assertEquals(
                    expected = expectedOutput1,
                    activeQueriesMetric.getMetricResponseMetadata(metricInput, metricOutput)
                )
            }
        }

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(
            activeQueriesInput3,
            activeQueriesInput4
        )) {
            for (metricOutput in listOf(metricOutput1, metricOutput2, metricOutput3)) {
                try {
                    activeQueriesMetric.getMetricResponseMetadata(
                        metricInput = metricInput,
                        metricOutput = metricOutput
                    )
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricOutput")
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
        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(activeQueriesInput1, activeQueriesInput2)) {
            for (metricConfig in listOf(metricConfig1, metricConfig2)) {
                try {
                    activeQueriesMetric.getMetricResult(
                        metricInput = metricInput,
                        metricConfig = metricConfig
                    )
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricConfig")
                } catch (e: SQLMonitoringConfigException) {
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
                SqlMetricManager.queryExecutor.execute<ActiveQueryDTO>(
                    databaseName,
                    "MetricMainQuery",
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf(activeQuery1, activeQuery2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(activeQuery1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<ActiveQueryDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }
        assertEquals(
            activeQueryResult1,
            activeQueriesMetric.getMetricResult(activeQueriesInput1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            activeQueryResult2,
            activeQueriesMetric.getMetricResult(activeQueriesInput2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            activeQueryResult3,
            activeQueriesMetric.getMetricResult(activeQueriesInput5, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )

        reset(mockConnection)
    }
}