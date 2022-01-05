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
import com.udaan.snorql.extensions.performance.models.IndexStatDTO
import com.udaan.snorql.extensions.performance.models.IndexStatInput
import com.udaan.snorql.extensions.performance.models.IndexStatResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class IndexStatsMetricTest {
    companion object {
        val indexStatsMetric = IndexStatsMetric()
    }

    // Index Stat Metric Main Query String
    private val indexStatMetricMainQuery: String? =
        indexStatsMetric.getMetricConfig(
            IndexStatInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = "randomDatabaseName",
                indexName = "randomIndexName",
                tableName = "randomTableName"
            ).metricId
        ).queries["main"]

    // Index Stats Metric Inputs
    private val indexStatsInput1 =
        IndexStatInput(
            metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName1",
            tableName = "randomTableName1", indexName = "randomIndexName1"
        )
    private val indexStatsInput2 =
        IndexStatInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName2",
            tableName = "randomTableName2",
            indexName = "randomIndexName2"
        )
    private val indexStatsInput3 =
        IndexStatInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName3",
            tableName = "randomTableName3",
            indexName = "randomIndexName3"
        )
    private val indexStatsInputIncorrectMetricId =
        IndexStatInput(
            metricId = "incorrectMetricId",
            metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName",
            tableName = "randomTableName",
            indexName = "randomIndexName"
        )
    private val indexStatsInputEmptyStringMetricId =
        IndexStatInput(
            metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName",
            tableName = "randomTableName", indexName = "randomIndexName"
        )

    // Metric configs
    private val metricConfig1 = MetricConfig(    // "main" query not defined in config
        queries = mapOf("notMain" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig2 = MetricConfig(    // empty queries map (no queries defined in config)
        queries = mapOf(),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // Index Statistics
    private val indexStats1 = IndexStatDTO(
        name = "indexName1",
        updated = "21-12-2021 22:08:44",
        rows = 233,
        rowsSampled = 190,
        steps = 23,
        density = 33,
        averageKeyLength = 23.44f,
        stringIndex = "Yes",
        filterExpression = "SomeExpression",
        unfilteredRows = 5,
        persistedSamplePercent = 33
    )
    private val indexStats2 =
        IndexStatDTO(
            name = null,
            updated = null,
            rows = null,
            rowsSampled = null,
            steps = null,
            density = null,
            averageKeyLength = null,
            stringIndex = null,
            filterExpression = null,
            unfilteredRows = null,
            persistedSamplePercent = null
        )

    // Index Stat Results
    private val indexStatMultipleResult = IndexStatResult(listOf(indexStats1, indexStats2)) // Multiple stats in result
    private val indexStatSingleResult = IndexStatResult(listOf(indexStats2))
    private val indexStatEmptyResult = IndexStatResult(listOf()) // empty result

    // Index Stat Output
    private val metricMultipleOutput = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatMultipleResult, null)
    private val metricSingleOutput = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatSingleResult, null)
    private val metricEmptyOutput = MetricOutput<IndexStatResult, IMetricRecommendation>(indexStatEmptyResult, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf(
            "underlyingQueries" to listOf(indexStatMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput1,
                metricMultipleOutput
            )
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput1,
                metricSingleOutput
            )
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput1,
                metricEmptyOutput
            )
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput2,
                metricMultipleOutput
            )
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput2,
                metricSingleOutput
            )
        )
        assertEquals(
            expected = expectedOutput1,
            indexStatsMetric.getMetricResponseMetadata(
                indexStatsInput2,
                metricEmptyOutput
            )
        )

        for (metricInput in listOf(indexStatsInputIncorrectMetricId, indexStatsInputEmptyStringMetricId)) {
            for (metricOutput in listOf(metricMultipleOutput, metricSingleOutput)) {
                try {
                    indexStatsMetric.getMetricResponseMetadata(metricInput, metricOutput)
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
        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(indexStatsInput1, indexStatsInput2)) {
            for (metricConfig in listOf(metricConfig1, metricConfig2)) {
                try {
                    indexStatsMetric.getMetricResult(metricInput, metricConfig)
                    fail("Exception not thrown for \nmetricInput = $metricInput \nmetricConfig = $metricConfig")
                } catch (e: SQLMonitoringConfigException) {
                    continue
                } catch (e: Exception) {
                    fail("Incorrect exception: $e \n thrown for metricInput = $metricInput \nmetricConfig = $metricConfig")
                }
            }
        }

        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        val metricInputList = listOf(
            indexStatsInput1, indexStatsInput2, indexStatsInput3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<IndexStatDTO>(
                    databaseName = metricInput.databaseName,
                    query = "MetricMainQuery",
                    mapOf("tableName" to metricInput.tableName, "indexName" to metricInput.indexName)
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf(indexStats1, indexStats2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(indexStats2)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<IndexStatDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }
        assertEquals(
            indexStatMultipleResult,
            indexStatsMetric.getMetricResult(indexStatsInput1, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            indexStatSingleResult,
            indexStatsMetric.getMetricResult(indexStatsInput2, TestHelper.metricConfigWithMainAndDbSizeQueries)
        )
        assertEquals(
            indexStatEmptyResult,
            indexStatsMetric.getMetricResult(indexStatsInput3, TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
    }
}