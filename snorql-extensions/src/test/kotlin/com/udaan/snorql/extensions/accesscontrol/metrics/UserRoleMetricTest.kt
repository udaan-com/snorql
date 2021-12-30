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

import com.udaan.snorql.extensions.TestHelper
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleDTO
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleInput
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
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
            databaseName = "randomDatabaseName").metricId).queries["main"]

    // Metric Configs
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
    private val metricConfig3 = MetricConfig(
        queries = mapOf("main" to "SELECT randomColumn from randomTable"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )
    private val metricConfig4 = MetricConfig(
        queries = mapOf("main" to ""),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    // User Role Metric Input
    private val userRoleMetricInput1 =
        UserRoleInput(metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase1")
    private val userRoleMetricInput2 =
        UserRoleInput(metricPeriod = MetricPeriod.HISTORICAL, databaseName = "randomDatabase1")
    private val userRoleMetricInput3 =
        UserRoleInput(metricId = "randomMetricID",
            metricPeriod = MetricPeriod.REAL_TIME,
            databaseName = "randomDatabase1")
    private val userRoleMetricInput4 =
        UserRoleInput(metricId = "", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "randomDatabase1")

    // Random User Role Metrics
    private val userRoleMetric1 = UserRoleDTO(name = "randomName", role = "admin", type = "SQLAdmin")
    private val userRoleMetric2 = UserRoleDTO(name = "randomName2", role = "manager", type = "External User")

    // User Role Metric Results
    private val userRoleResult1 = UserRoleResult(listOf(userRoleMetric1, userRoleMetric2))
    private val userRoleResult2 = UserRoleResult(listOf(userRoleMetric2))
    private val userRoleResult3 = UserRoleResult(listOf()) // Empty result

    // User Role Outputs
    private val metricOutput1 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResult1, null)
    private val metricOutput2 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResult2, null)
    private val metricOutput3 = MetricOutput<UserRoleResult, IMetricRecommendation>(userRoleResult3, null)

    @Test
    fun testGetMetricResponseMetadata() {
        val expectedOutput1 = mapOf<String, Any?>(
            "underlyingQueries" to listOf(userRoleMetricMainQuery),
            "referenceDocumentation" to "",
            "description" to ""
        )
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput1, metricOutput1))
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput1, metricOutput2))
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput1, metricOutput3))
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput2, metricOutput1))
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput2, metricOutput2))
        assertEquals(expected = expectedOutput1,
            userRoleMetric.getMetricResponseMetadata(userRoleMetricInput2, metricOutput3))

        for (metricInput in listOf(userRoleMetricInput3, userRoleMetricInput4)) {
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

        // TODO: success case

        // Testing for SQLMonitoringConfigException
        for (metricInput in listOf(userRoleMetricInput1, userRoleMetricInput2, userRoleMetricInput3, userRoleMetricInput4)) {
            for (metricConfig in listOf(TestHelper.metricConfigWithoutMainQuery, TestHelper.metricConfigWithoutQueries)) {
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