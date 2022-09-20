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

package com.udaan.snorql.extensions.performance.metrics

import java.text.SimpleDateFormat
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagDTO
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagInput
import com.udaan.snorql.extensions.performance.models.ReadReplicationLagResult
import com.udaan.snorql.extensions.performance.models.ReadReplicationRecommendation
import com.udaan.snorql.extensions.performance.models.ReplicationStateDTO
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

/**
 * Class which implements the ReadReplicationLag metric
 *
 * <p> Gives replication lag </p>
 *
 * @constructor Create ReadReplicationLag metric
 */
class ReadReplicationLagMetric :
    IMetric<ReadReplicationLagInput, ReadReplicationLagResult, IMetricRecommendation> {

    companion object {
        private val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        private const val THRESHOLD_LAG_IN_MILLIS = 120000
    }

    override fun getMetricResult(
        metricInput: ReadReplicationLagInput,
        metricConfig: MetricConfig
    ): ReadReplicationLagResult {
        // check the metricConfig.supportedHistory before getting the query
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under " +
                        "config [${metricInput.metricId}]")

        val response = SqlMetricManager.queryExecutor
            .execute<ReplicationStateDTO>(metricInput.replicaDatabaseName, query)
        if(response.isEmpty()) throw Exception("No replicas found.")

        val lastReceivedTime = sdf.parse(response[0].lastReceivedTime)
        val lastRedoneTime = sdf.parse(response[0].lastRedoneTime)
        val replicationLag = lastReceivedTime.time - lastRedoneTime.time

        return ReadReplicationLagResult(
            queryList = listOf(
                ReadReplicationLagDTO(
                    replicationLagInMillis = replicationLag,
                    lastReceivedTimeInMillis = lastReceivedTime.time,
                    lastRedoneTimeInMillis = lastRedoneTime.time
                )
            )
        )
    }

    override fun getMetricResponseMetadata(
        metricInput: ReadReplicationLagInput,
        metricOutput: MetricOutput<ReadReplicationLagResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description
        responseMetadata["supportsHistorical"] = metricConfig.supportsHistorical
        responseMetadata["minimumRepeatInterval"] = metricConfig.persistDataOptions?.get("minimumRepeatInterval") ?: ""
        responseMetadata["supportsAlert"] = metricConfig.supportsAlert
        responseMetadata["supportedAlerts"] = metricConfig.alertingOptions?.get("supportedAlerts") ?: listOf<String>()
        return responseMetadata
    }

    override fun getMetricRecommendations(
        metricInput: ReadReplicationLagInput,
        metricResult: ReadReplicationLagResult
    ): ReadReplicationRecommendation? {
        if(metricInput.recommendationRequired) {
            val readReplicationLagDTO = metricResult.queryList[0]
            return if (readReplicationLagDTO.replicationLagInMillis < THRESHOLD_LAG_IN_MILLIS)
                ReadReplicationRecommendation("No significant replication lag. No action required.", false)
            else ReadReplicationRecommendation(
                "Replication lag is more than 120s. Please check any long running queries which might cause this.",
                false
            )
        }
        return null
    }


    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}