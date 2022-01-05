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

package com.udaan.snorql.framework.metric

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.reset
import com.udaan.snorql.framework.SQLMonitoringException
import com.udaan.snorql.framework.models.Configuration
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import com.udaan.snorql.framework.models.MetricResponse
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class SqlMetricManagerTest {
    // Demo Metric Input
    private val metricInput1 =
        DemoInput(metricId = "DemoMetricId1", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "DemoDatabaseName1")

    // Demo DTOs
    private val demoDTO1 = DemoDTO(
        demoField1 = "RandomString1",
        demoField2 = 1
    )
    private val demoDTO2 = DemoDTO(
        demoField1 = "RandomString2",
        demoField2 = 2
    )

    // Demo metric output
    private val metricOutput1 = MetricOutput(
        result = DemoResult(listOf(demoDTO1, demoDTO2)),
        recommendation = null
    )

    // Demo Metric Response
    private val metricResponse1 = MetricResponse(metricInput1, metricOutput1, null) // mapOf("Metadata1" to "MetadataValue1"))

    private val CONFIG_FILE_LOCATION = "/sql-monitoring-conf.json"

    @Before
    fun beforeTests() {
        mockkObject(SqlMetricManager)
    }

    @Test
    fun testLogger() {
        SqlMetricManager.logger.warn("This is a warning")


    }

    @Test
    fun testConfiguration() {
        assertTrue { SqlMetricManager.configuration is Configuration }
        assertNotNull(SqlMetricManager.configuration)
    }

    @Test
    fun testGetMetric() {
        // Failing case (metricId = "testIMetric" does not exist)
        try {
            SqlMetricManager.getMetric<DemoInput, DemoResult, DemoRecommendation>(
                metricId = "testIMetric",
                metricInput = metricInput1
            )
            fail("SqlMetricManager.getMetric does not throw an exception for metricId: testIMetric")
        } catch (e: SQLMonitoringException) {
            assertEquals("IMetric impl instance not found for metric id [testIMetric]", e.message)
        } catch (e: Exception) {
            fail("Test failed with unexpected exception: $e\nFor metircId: testIMetric")
        }

        // Adding metric with metricId: "testIMetric"
        SqlMetricManager.addMetric("testIMetric", TestIMetric())

        // Success Test Case
        assertEquals(
            metricResponse1, SqlMetricManager.getMetric<DemoInput, DemoResult, DemoRecommendation>(
                metricId = "testIMetric",
                metricInput = metricInput1
            )
        )
    }

    // Use mockk to mock SqlMetricManager.kt
    @Test
    fun testSetConnection() {
        // Setting a mock connection instance
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)

        // Using queryExecutor with a connection instance
        assertEquals(mockConnection, SqlMetricManager.queryExecutor.connection)

        reset(mockConnection)
    }

    @After
    fun afterTests() {
        // Unmocks all mocks
        unmockkAll()
    }
}