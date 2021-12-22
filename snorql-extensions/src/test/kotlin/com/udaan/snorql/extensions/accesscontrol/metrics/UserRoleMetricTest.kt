package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.accesscontrol.models.UserRoleDTO
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleInput
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import org.junit.Test
import kotlin.test.assertEquals

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
    }
    
    @Test(expected = SQLMonitoringConnectionException::class)
    fun testSQLMonitoringConnectionException() {
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput1, metricConfig = metricConfig3)
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput2, metricConfig = metricConfig4)
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput1, metricConfig = metricConfig4)
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput2, metricConfig = metricConfig3)
    }

    @Test(expected = SQLMonitoringConfigException::class)
    fun testSQLMonitoringConfigException() {
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput1,
            metricConfig = metricConfig1)
        userRoleMetric.getMetricResult(metricInput = userRoleMetricInput1,
            metricConfig = metricConfig2)
        userRoleMetric.getMetricResponseMetadata(metricInput = userRoleMetricInput1, metricOutput = metricOutput1)
        userRoleMetric.getMetricResponseMetadata(metricInput = userRoleMetricInput2, metricOutput = metricOutput1)
        userRoleMetric.getMetricResponseMetadata(metricInput = userRoleMetricInput3, metricOutput = metricOutput2)
        userRoleMetric.getMetricResponseMetadata(metricInput = userRoleMetricInput4, metricOutput = metricOutput3)
        userRoleMetric.getMetricResponseMetadata(metricInput = userRoleMetricInput3, metricOutput = metricOutput3)
    }
}