package com.udaan.snorql.framework.models

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
    val startFrom: Timestamp
    val endAt: Timestamp?
    val description: String?
    val configuredByName: String?
    val configuredByEmail: String?
}

data class TriggerBuildConfig(
    val triggerName: String,
    val description: String?,
    val job: JobDetail?,
    val jobDataMap: JobDataMap,
    val intervalInSeconds: Int,
    val endAt: Timestamp?
)

data class HistoricalDatabaseSchemaDTO(
    val runId: String,
    val timestamp: Timestamp,
    val metricId: String,
    val databaseName: String,
    val source: String,
    val metricInput: String,
    val metricOutput: String
)
