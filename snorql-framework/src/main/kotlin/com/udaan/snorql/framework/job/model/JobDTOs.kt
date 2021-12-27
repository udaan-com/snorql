package com.udaan.snorql.framework.job.model

import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput
import com.udaan.snorql.framework.models.MetricPeriod
import java.sql.Timestamp

data class JobTriggerConfig(
//    val metricInput: ActualMetricInput,
    val watchIntervalInSeconds: Int,
    val startFrom: Timestamp? = null,
    val endAt: Timestamp? = null,
)

abstract class RecordingJobConfigOutline {
    abstract val watchIntervalInSeconds: Int
    abstract val startFrom: Timestamp
    abstract val endAt: Timestamp?
}

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

data class ActualMetricOutput(val queryList: List<ActualQueryDTO>) : IMetricResult()

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


/**
 * Class which implements Active Queries Metric
 *
 * <p>The active queries metric fetches the active queries for the
 * specified database along with other metadata.</p>
 *
 * @constructor Create Active queries metric
 */
class ActiveQueriesMetric : IMetric<ActualMetricInput, ActualMetricOutput, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: ActualMetricInput,
        metricConfig: MetricConfig,
    ): ActualMetricOutput {
        // check the metricConfig.supportedHistory before getting the query
//        val query =
//            metricConfig.queries["main"]
//                    ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")
//
//        val result = SqlMetricManager.queryExecutor.execute<ActualQueryDTO>(metricInput.databaseName, query)

        return ActualMetricOutput(listOf())
    }

    override fun getMetricResponseMetadata(
        metricInput: ActualMetricInput,
        metricOutput: MetricOutput<ActualMetricOutput, IMetricRecommendation>,
    ): Map<String, Any>? {
//        val responseMetadata = mutableMapOf<String, Any>()
//        val query =
//            getMetricConfig(metricInput.metricId).queries["main"]
//        responseMetadata["underlyingQueries"] = listOf(query)
        return mapOf()
    }


    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}

data class HistoricalDatabaseSchemaDTO(
    val runId: String,
    val timestamp: Timestamp,
    val metricId: String,
    val databaseName: String,
    val source: String,
    val metricInput: MetricInput,
    val metricOutput: MetricOutput<out IMetricResult, out IMetricRecommendation>
)

data class ActualQueryDTO(
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
)
