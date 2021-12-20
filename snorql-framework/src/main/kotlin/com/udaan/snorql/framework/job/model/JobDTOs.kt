package com.udaan.snorql.framework.job.model

import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import java.sql.Timestamp

data class TriggerConfig(
//    val metricInput: ActualMetricInput,
    val watchIntervalInSeconds: Int,
    val startFrom: Timestamp? = null,
    val endAt: Timestamp? = null,
)

data class ActualMetricInput(
    override val metricId: String,
    override val metricPeriod: MetricPeriod = MetricPeriod.REAL_TIME,
    override val databaseName: String,
) : MetricInput() {
    fun toJsonString(): String {
        return """
            "metricId": $metricId,
            "metricPeriod": $metricPeriod,
            "databaseName": $databaseName
        """.trimIndent()
    }
}

data class ActualMetricOutput(val queryList: List<String>) : IMetricResult()