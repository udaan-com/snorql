package com.udaan.snorql.framework.models

import java.sql.Timestamp

data class JobTriggerConfig(
    val watchIntervalInSeconds: Int,
    val startFrom: Timestamp? = null,
    val endAt: Timestamp? = null,
)

abstract class RecordingJobConfigOutline {
    abstract val databaseName: String
    abstract val watchIntervalInSeconds: Int
    abstract val startFrom: Timestamp
    abstract val endAt: Timestamp?
    abstract val description: String?
    abstract val configuredByName: String?
    abstract val configuredByEmail: String?
}

data class HistoricalDatabaseSchemaDTO(
    val runId: String,
    val timestamp: Timestamp,
    val metricId: String,
    val databaseName: String,
    val source: String,
    val metricInput: String,
    val metricOutput: String
)
