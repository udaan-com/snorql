package com.udaan.snorql.extensions.session.models

import com.udaan.snorql.extensions.session.SessionEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

data class LatestExecutedQueryDTO(
    @ColumnName("event_type")
    val eventType: String,
    val parameters: String,
    @ColumnName("event_info")
    val eventInfo: String
)

data class LatestExecutedQueryInput(
    override val metricId: String = SessionEnums.LATEST_EXECUTED_QUERY.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val sessionId: Int
) : MetricInput()

data class LatestExecutedQueryResult(val queryList: List<LatestExecutedQueryDTO>) : IMetricResult()
