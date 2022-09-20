package com.udaan.snorql.framework.models

import org.quartz.CronExpression
import org.quartz.JobDataMap
import org.quartz.JobDetail
import java.sql.Timestamp

data class JobTriggerConfig(
    val watchIntervalInSeconds: Int,
    val startFrom: Timestamp? = null,
    val endAt: Timestamp? = null,
)

interface RecordingJobConfigOutline {
    val databaseName: String
    val watchIntervalInSeconds: Int
    val endAt: Timestamp?
    val startFrom: Timestamp?
    val description: String?
    val configuredByName: String?
    val configuredByEmail: String?
    val dataRetentionPeriodInDays: Int
}

data class TriggerBuildConfig(
    val triggerName: String,
    val description: String?,
    val job: JobDetail?,
    val jobDataMap: JobDataMap,
    val intervalInSeconds: Int,
    val endAt: Timestamp?
)

data class CronTriggerBuildConfig(
    val triggerName: String,
    val description: String?,
    val job: JobDetail?,
    val jobDataMap: JobDataMap?,
    val cronExpression: CronExpression,
    val endAt: Timestamp?
)

data class HistoricalDatabaseResult(
    val result: List<HistoricalDatabaseSchemaDTO>,
    val metadata: Map<String, Any>?
)

data class HistoricalDatabaseSchemaDTO(
    val runId: String,
    val timestamp: String,
    val metricId: String,
    val databaseName: String,
    val source: String,
    val metricInput: String,
    val metricOutput: String,
)

data class HistoricalDataPurgeConfig(
    val metricId: String,
    val databaseName: String,
    val purgeDataOlderThan: Timestamp
)
