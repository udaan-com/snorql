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
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.performance.models.ActiveDDLDTO
import com.udaan.snorql.extensions.performance.models.ActiveDDLInput
import com.udaan.snorql.extensions.performance.models.ActiveDDLResult
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

class ActiveDDLMetricTest {
    companion object {
        private val activeDDLMetric = ActiveDDLMetric()
    }

    private val activeDDLMetricMainQuery: String? =
        activeDDLMetric.getMetricConfig(
            ActiveDDLInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = "randomDatabaseName"
            ).metricId
        ).queries["main"]

    // Active DDL Query Input
    private val activeDDLInput1 =
        ActiveDDLInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val activeDDLInput2 =
        ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val activeDDLIncorrectMetricIdInput = ActiveDDLInput(
        metricId = "randomIncorrectID", // Incorrect Metric ID passed
        metricPeriod = MetricPeriod.REAL_TIME,
        databaseName = "randomDatabaseName"
    )
    private val activeDDLEmptyStringMetricIdInput = ActiveDDLInput(
        metricId = "", // Empty string metricID
        metricPeriod = MetricPeriod.HISTORICAL,
        databaseName = "randomDatabaseName"
    )
    private val activeDDLInput5 =
        ActiveDDLInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName3")

    // Random active ddl query 1
    private val activeDDLQuery1 = ActiveDDLDTO(
        currentStep = "1 Current Step",
        queryText = "SELECT randomColumn1 FROM randomTable1",
        totalRows = 51,
        rowsProcessed = 42,
        rowsLeft = 9,
        percentComplete = 78.42f,
        elapsedSeconds = 128,
        estimatedSecondsLeft = 184,
        estimatedCompletionTime = "21-12-2021 22:56:45"
    )

    // Random active ddl query 2
    private val activeDDLQuery2 = ActiveDDLDTO(
        currentStep = "2 Current Step",
        queryText = "SELECT randomColumn2 FROM randomTable2",
        totalRows = 23,
        rowsProcessed = 12,
        rowsLeft = 11,
        percentComplete = 78.42f,
        elapsedSeconds = 123,
        estimatedSecondsLeft = 186,
        estimatedCompletionTime = "21-12-2021 22:58:45"
    )

    // Mock ActiveDDLMetric Result
    private val activeDDLMultipleResult = ActiveDDLResult(listOf(activeDDLQuery1, activeDDLQuery2)) // Multiple queries
    private val activeDDLSingleResult = ActiveDDLResult(listOf(activeDDLQuery1)) // Single Query
    private val activeDDLEmptyResult = ActiveDDLResult(listOf()) // No queries in result

    // Mock MetricOutput
    private val metricMultipleOutput =
        MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLMultipleResult, null)
    private val metricSingleOutput = MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLSingleResult, null)
    private val metricEmptyOutput = MetricOutput<ActiveDDLResult, IMetricRecommendation>(activeDDLEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        // Expected getMetricResponseMetadata output 1
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(activeDDLMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )

        for (metricInput in listOf(activeDDLInput1, activeDDLInput2)) {
            for (metricOutput in listOf(metricMultipleOutput, metricSingleOutput, metricEmptyOutput)) {
                assertEquals(
                    expected = expectedOutput1,
                    activeDDLMetric.getMetricResponseMetadata(metricInput, metricOutput)
                )
            }
        }

        for (metricInput in listOf(activeDDLIncorrectMetricIdInput, activeDDLEmptyStringMetricIdInput)) {
            for (metricOutput in listOf(metricEmptyOutput, metricSingleOutput, metricMultipleOutput)) {
                try {
                    activeDDLMetric.getMetricResponseMetadata(metricInput, metricOutput)
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
        for (metricInput in listOf(activeDDLInput1, activeDDLInput2, activeDDLIncorrectMetricIdInput, activeDDLEmptyStringMetricIdInput)) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainAndDbSizeQueries,
                TestHelper.metricConfigWithoutMainQuery
            )) {
                try {
                    activeDDLMetric.getMetricResult(metricInput, metricConfig)
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
                SqlMetricManager.queryExecutor.execute<ActiveDDLDTO>(
                    databaseName,
                    "MetricMainQuery"
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf(activeDDLQuery1, activeDDLQuery2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(activeDDLQuery1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<ActiveDDLDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            activeDDLMultipleResult, activeDDLMetric.getMetricResult(
                activeDDLInput1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            activeDDLSingleResult, activeDDLMetric.getMetricResult(
                activeDDLInput2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            activeDDLEmptyResult, activeDDLMetric.getMetricResult(
                activeDDLInput5,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        reset(mockConnection)
    }
}