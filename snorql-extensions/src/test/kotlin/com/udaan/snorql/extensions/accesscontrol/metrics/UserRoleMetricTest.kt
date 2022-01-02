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

package com.udaan.snorql.extensions.accesscontrol.metrics

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleDTO
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleInput
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleResult
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

class UserRoleMetricTest {
    companion object {
        private val userRoleMetric = UserRoleMetric()
    }

    private val userRoleMetricMainQuery: String? = userRoleMetric.getMetricConfig(
        UserRoleInput(
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabaseName"
        ).metricId
    ).queries["main"]

    // User Role Metric Input
    private val userRoleMetricInputRealTime1 =
        UserRoleInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName1")
    private val userRoleMetricInputHistorical2 =
        UserRoleInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabaseName2")
    private val userRoleMetricInputRealTime3 =
        UserRoleInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabaseName3")
    private val userRoleMetricInputIncorrectMetricId =
        UserRoleInput(
            metricId = "randomMetricID",
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabase1"
        )
    private val userRoleMetricInputEmptyMetricId =
        UserRoleInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase1")

    // Random User Role Metrics
    private val userRoleMetric1 = UserRoleDTO(name = "randomName", role = "admin", type = "SQLAdmin")
    private val userRoleMetric2 = UserRoleDTO(name = "randomName2", role = "manager", type = "External User")

    // User Role Metric Results
    private val userRoleResultMultiple = UserRoleResult(listOf(userRoleMetric1, userRoleMetric2))
    private val userRoleResultSingle = UserRoleResult(listOf(userRoleMetric1))
    private val userRoleResultEmpty = UserRoleResult(listOf()) // Empty result

    // User Role Outputs
    private val metricOutput1 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResultMultiple, null)
    private val metricOutput2 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResultSingle, null)
    private val metricOutput3 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResultEmpty, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(userRoleMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputRealTime1, metricOutput1)
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputRealTime1, metricOutput2)
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputRealTime1, metricOutput3)
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputHistorical2, metricOutput1)
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputHistorical2, metricOutput2)
        )
        assertEquals(
            expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInputHistorical2, metricOutput3)
        )

        for (metricInput in listOf(userRoleMetricInputIncorrectMetricId, userRoleMetricInputEmptyMetricId)) {
            for (metricOutput in listOf(metricOutput1, metricOutput2)) {
                try {
                    userRoleMetric.getMetricResponseMetadata(metricInput, metricOutput)
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
            userRoleMetricInputRealTime1, userRoleMetricInputHistorical2, userRoleMetricInputRealTime3
        )
        metricInputList.forEach { metricInput ->
            whenever(
                SqlMetricManager.queryExecutor.execute<UserRoleDTO>(
                    metricInput.databaseName,
                    "MetricMainQuery",
                )
            ).thenAnswer {
                val database: String = it.getArgument(0) as String
                val query: String = it.getArgument(1) as String
                when {
                    (database == "randomDatabaseName1") -> {
                        listOf<UserRoleDTO>(userRoleMetric1, userRoleMetric2)
                    }
                    (database == "randomDatabaseName2") -> {
                        listOf(userRoleMetric1)
                    }
                    (database == "randomDatabaseName3") -> {
                        listOf<UserRoleDTO>()
                    }
                    else -> {
                        throw IllegalArgumentException("Arguments does not match: Database Name: $database; Query: $query")
                    }
                }
            }
        }

        assertEquals(
            userRoleResultMultiple,
            userRoleMetric.getMetricResult(
                userRoleMetricInputRealTime1,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            userRoleResultSingle,
            userRoleMetric.getMetricResult(
                userRoleMetricInputHistorical2,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )
        assertEquals(
            userRoleResultEmpty,
            userRoleMetric.getMetricResult(
                userRoleMetricInputRealTime3,
                TestHelper.metricConfigWithMainAndDbSizeQueries
            )
        )

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(
            userRoleMetricInputRealTime1,
            userRoleMetricInputHistorical2,
            userRoleMetricInputIncorrectMetricId,
            userRoleMetricInputEmptyMetricId
        )) {
            for (metricConfig in listOf(
                TestHelper.metricConfigWithoutMainQuery,
                TestHelper.metricConfigWithoutQueries
            )) {
                try {
                    userRoleMetric.getMetricResult(metricInput, metricConfig)
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