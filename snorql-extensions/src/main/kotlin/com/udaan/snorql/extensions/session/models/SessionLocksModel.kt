package com.udaan.snorql.extensions.session.models

import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

data class SessionLocksInput(
    override val metricId: String,
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val sessionId: Int
): MetricInput()

data class SessionLocksDTO(
    @ColumnName("request_session_id")
    val sessionId: Int,
    @ColumnName("resource_database_id")
    val databaseId: Int,
    @ColumnName("request_lifetime")
    val requestLifetime: Long,
    @ColumnName("dbname")
    val dbName: String,
    @ColumnName("ObjectName")
    val objectName: String,
    @ColumnName("index_id")
    val indexId: Int,
    @ColumnName("index_name")
    val indexName: String,
    @ColumnName("resource_type")
    val resourceType: String,
    @ColumnName("resource_description")
    val resourceDescription: String,
    @ColumnName("resource_associated_entity_id")
    val resourceAssociatedEntityId: String,
    @ColumnName("request_mode")
    val requestMode: String,
    @ColumnName("request_status")
    val requestStatus: String
)

data class SessionLocksResult(val queryList: List<SessionLocksDTO>): IMetricResult()