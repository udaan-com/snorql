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

package com.udaan.snorql.extensions

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.QueryExecutor
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.MetricConfig

object TestHelper {

    // Metric configs
    val metricConfigWithMainAndDbSizeQueries =
        MetricConfig(
            queries = mapOf("main" to "MetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutMainQuery =
        MetricConfig(
            queries = mapOf("notMain" to "MetricMainQuery", "dbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutDbSizeQuery =
        MetricConfig(
            queries = mapOf("main" to "MetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutMainAndDbSizeQueries =
        MetricConfig(
            queries = mapOf("notMain" to "MetricMainQuery", "notDbSize" to "dbSizeQueryString"),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithoutQueries =
        MetricConfig(
            queries = mapOf(),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )
    val metricConfigWithEmptyStringMainQuery =
        MetricConfig(
            queries = mapOf("main" to ""),
            description = "randomDescription",
            isParameterized = true,
            referenceDoc = listOf<String>(""),
            supportsHistorical = true,
            supportsRealTime = true
        )

    inline fun <reified T> mockSqlMetricManager(
        outputMetricList: List<T>
    ): SqlMetricManager {
        val mockConnectionInstance: Connection = mock()
//        SqlMetricManager.setConnection(mockConnectionInstance)
        val sqlMetricManager: SqlMetricManager = mock()
        whenever(sqlMetricManager.queryExecutor).thenReturn(QueryExecutor(mockConnectionInstance))
        whenever(
            sqlMetricManager.queryExecutor.execute<T>(
//                eq(ArgumentMatchers.anyString()),
//                eq(ArgumentMatchers.anyString())
                databaseName = "randomDatabaseName1",
                query = "MetricMainQuery"
            )
        ).thenAnswer {
            val queryString = it.arguments[1]
//            println("List: $outputMetricList")
            when {
                (queryString == metricConfigWithMainAndDbSizeQueries.queries["main"]) -> {
                    outputMetricList
                }
                (queryString == metricConfigWithMainAndDbSizeQueries.queries["dbSize"]) -> {
                    outputMetricList
                }
                else -> {
                    throw IllegalArgumentException("neither a main query nor db size query: $queryString")
                }
            }
        }
//        println("List: $outputMetricList")
        return sqlMetricManager
    }
}