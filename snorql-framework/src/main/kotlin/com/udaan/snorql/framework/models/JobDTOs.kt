/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
