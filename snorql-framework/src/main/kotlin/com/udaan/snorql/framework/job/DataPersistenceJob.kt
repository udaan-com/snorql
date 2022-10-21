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

package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.UUID
import java.util.TimeZone
import java.util.Date

class DataPersistenceJob<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> : Job {
    val logger = SqlMetricManager.logger

    /**
     * Function executed when a data recording trigger is fired
     * The function does the following:
     * 1. Generates a random runID
     * 2. Receives merged data map (contains data required for execution)
     * 3. Fetches metric data
     * 4. Calls the function to store data in historical data store
     */
    override fun execute(context: JobExecutionContext) {
        val runID: String = UUID.randomUUID().toString()
        logger.info("Data Persistence job execution started for runID: $runID")
        val mergedDataMap = context.mergedJobDataMap
        try {
            val metricInput: T =
                SnorqlConstants.objectMapper.readValue(
                    mergedDataMap["metricInput"] as String,
                    Class.forName(mergedDataMap["inputClass"] as String)
                ) as T
            val metricResponse = SqlMetricManager.getMetric<T, O, R>(metricInput.metricId, metricInput)
            val metricOutput = metricResponse.metricOutput
            val timestamp = currentTimestampInISOString()
            val dataRecorded = HistoricalDatabaseSchemaDTO(
                runId = "${timestamp}_$runID",
                timestamp = timestamp,
                metricId = metricInput.metricId,
                databaseName = metricInput.databaseName,
                source = SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME,
                metricInput = SnorqlConstants.objectMapper.writeValueAsString(metricInput),
                metricOutput = SnorqlConstants.objectMapper.writeValueAsString(metricOutput)
            )
            val storageId = SnorqlConstants.HISTORICAL_DATA_BUCKET_ID
            SqlMetricManager.queryExecutor.persistHistoricalData(storageId, listOf(dataRecorded))
            logger.info("Data persisted for runID: ${dataRecorded.runId}")
        } catch (e: Exception) {
            logger.error(
                "[DataPersistenceJob] Error while persisting metric data: ${e.message}\n" +
                        "Metric Input: ${mergedDataMap["metricInput"]}", e
            )
        }
    }

    private fun currentTimestampInISOString(): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df: DateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") // Quoted "Z" to indicate UTC, no timezone offset
        df.timeZone = tz
        return df.format(Date())
    }
}
