package com.udaan.snorql.framework.job.model

import java.sql.Timestamp

abstract class JobConfig {
    abstract val metricId: String
    abstract val databaseName: String
    abstract val jobId: String
    abstract val triggerId: String
    abstract val watchInterval: Int
    val startFrom: Timestamp? = null
    val endAt: Timestamp? = null
}