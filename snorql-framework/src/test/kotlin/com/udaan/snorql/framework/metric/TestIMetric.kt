package com.udaan.snorql.framework.metric

import com.udaan.snorql.framework.models.*
import junit.framework.Assert.assertNotNull
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
        recommendation = null
    ) // DemoRecommendation(listOf("Recommendation String 1", "Recommendation String 2"))

    private val metricOutput2 = MetricOutput(
        result = demoResult1,
        recommendation = null
    )

    private val metricResponse1 = MetricResponse(metricInput1, metricOutput1, mapOf("Metadata1" to "MetadataValue1"))
    private val metricResponse2 = MetricResponse(metricInput2, metricOutput2, mapOf("Metadata1" to "MetadataValue1"))

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

    override fun getMetricRecommendations(metricInput: DemoInput, metricResult: DemoResult): DemoRecommendation {
        return DemoRecommendation(listOf("Recommendation String 1", "Recommendation String 2"))
    }

    override fun getMetricResponseMetadata(
        metricInput: DemoInput,
        metricOutput: MetricOutput<DemoResult, DemoRecommendation>
    ): Map<String, Any>? {
        return mapOf("Metadata1" to "MetadataValue1")
    }

    override fun getMetricConfig(metricId: String): MetricConfig {
        return if (metricId == metricInput1.metricId) {
            metricConfig1
        } else {
            metricConfig2
        }
    }

    @Test
    fun testGetMetricResponse() {
        assertEquals(metricResponse1, getMetricResponse(metricInput1))
        assertEquals(metricResponse2, getMetricResponse(metricInput2))
    }

    @Test
    fun testGetMetricRecommendations() {
        assertNotNull(getMetricRecommendations(metricInput1, demoResult1))
        assertNotNull(getMetricRecommendations(metricInput2, demoResult1))
    }
}