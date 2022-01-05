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

import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.models.*
import junit.framework.Assert.assertNotNull
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

data class DemoDTO(
    val demoField1: String,
    val demoField2: Int
)

data class DemoInput(
    override val metricId: String,
    override val metricPeriod: MetricPeriod,
    override val databaseName: String
) : MetricInput()

data class DemoResult(val queryList: List<DemoDTO>) : IMetricResult()

data class DemoRecommendation(val recommendationList: List<String>) : IMetricRecommendation()

class TestIMetric : IMetric<DemoInput, DemoResult, DemoRecommendation> {

    private val metricConfig1 = MetricConfig(
        queries = mapOf("main" to "MainQuery"),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "SomeReferenceDoc",
        description = "Some Description"
    )

    private val metricConfig2 = MetricConfig(
        queries = mapOf("main" to "MainQuery"),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "SomeReferenceDoc",
        description = "Some Description"
    )

    private val demoDTO1 = DemoDTO(
        demoField1 = "RandomString1",
        demoField2 = 1
    )
    private val demoDTO2 = DemoDTO(
        demoField1 = "RandomString2",
        demoField2 = 2
    )

    private val metricInput1 =
        DemoInput(metricId = "DemoMetricId1", metricPeriod = MetricPeriod.REAL_TIME, databaseName = "DemoDatabaseName1")
    private val metricInput2 = DemoInput(
        metricId = "DemoMetricId2",
        metricPeriod = MetricPeriod.HISTORICAL,
        databaseName = "DemoDatabaseName2"
    )

    private val demoResult1 = DemoResult(listOf(demoDTO1))

    private val metricOutput1 = MetricOutput(
        result = DemoResult(listOf(demoDTO1, demoDTO2)),
        recommendation = getMetricRecommendations(metricInput1, getMetricResult(metricInput1, metricConfig1))
    ) // DemoRecommendation(listOf("Recommendation String 1", "Recommendation String 2"))

    private val metricOutput2 = MetricOutput(
        result = demoResult1,
        recommendation = getMetricRecommendations(metricInput2, getMetricResult(metricInput2, metricConfig2))
    )

    private val metricResponse1 = MetricResponse(metricInput1, metricOutput1, null) // mapOf("Metadata1" to "MetadataValue1"))
    private val metricResponse2 = MetricResponse(metricInput2, metricOutput2, null) // mapOf("Metadata1" to "MetadataValue1"))

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }

    override fun getMetricResult(metricInput: DemoInput, metricConfig: MetricConfig): DemoResult {
        return when (metricInput.metricId) {
            "DemoMetricId1" -> {
                DemoResult(listOf(demoDTO1, demoDTO2))
            }
            "DemoMetricId2" -> {
                DemoResult(listOf(demoDTO1))
            }
            else -> {
                DemoResult(listOf(demoDTO1))
            }
        }
    }

//    override fun getMetricRecommendations(metricInput: DemoInput, metricResult: DemoResult): DemoRecommendation {
//        return DemoRecommendation(listOf("Recommendation String 1", "Recommendation String 2"))
//    }

//    override fun getMetricResponseMetadata(
//        metricInput: DemoInput,
//        metricOutput: MetricOutput<DemoResult, DemoRecommendation>
//    ): Map<String, Any>? {
//        return mapOf("Metadata1" to "MetadataValue1")
//    }

    @Test
    fun testGetMetricResponse() {
        assertEquals(metricResponse1, getMetricResponse(metricInput1))
    }

    @Test
    fun testGetMetricRecommendations() {
        // Testing Recommendations
        assertNull(getMetricRecommendations(metricInput1, demoResult1))
        assertNull(getMetricRecommendations(metricInput2, demoResult1))
//        assertNotNull(getMetricRecommendations(metricInput1, demoResult1))
//        assertNotNull(getMetricRecommendations(metricInput2, demoResult1))
    }

    @Test
    fun testGetMetricResponseMetadata() {
        assertNull(getMetricResponseMetadata(metricInput1, metricOutput1))
        assertNull(getMetricResponseMetadata(metricInput2, metricOutput2))
    }

    @Test
    fun testGetMetricConfig() {
        // Added a demo metric configuration in test sql-monitoring-conf.json
        assertEquals(metricConfig1, getMetricConfig(metricInput1.metricId))

        // Failing Test Case - When configuration does not exist in sql-monitoring-conf.json
        val doesNotExistMetricId = "doesNotExistMetricConfig"
        try {
            getMetricConfig(doesNotExistMetricId)
            fail("Exception not thrown by getMetricConfig for metricId: $doesNotExistMetricId")
        } catch (e: SQLMonitoringConfigException) {
            assertEquals("Config against metric id $doesNotExistMetricId not found", e.message)
        } catch (e: Exception) {
            fail("Unexpected exception thrown by getMetricConfig for metricId:$doesNotExistMetricId")
        }
    }
}