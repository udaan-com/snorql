package com.udaan.snorql.extensions.session.models

import com.udaan.snorql.extensions.session.SessionEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod

data class SessionActiveQueryDTO(
    val sessionId: Int,
    val status: String,
    val blockedBy: Int,
    val waitType: String?,
    val waitResource: String?,
    val percentComplete: Int,
    val waitTime: String?,
    val cpuTime: Int?,
    val logicalReads: Int?,
    val reads: Int?,
    val writes: Int?,
    val elapsedTime: String,
    val queryText: String,
    val storedProc: String,
    val command: String,
    val loginName: String,
    val hostName: String,
    val programName: String,
    val hostProcessId: Int,
    val lastRequestEndTime: String,
    val loginTime: String,
    val openTransactionCount: Int,
    val xmlPlan: String
)

data class SessionActiveQueryInput(
    override val metricId: String = SessionEnums.SESSION_ACTIVE_QUERY.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val sessionId: Int
) : MetricInput()

data class SessionActiveQueryResult(val queryList: List<SessionActiveQueryDTO>) : IMetricResult()
