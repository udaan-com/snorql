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

import com.udaan.snorql.extensions.performance.models.GeoReplicaLagDTO
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagInput
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagRecommendation
import com.udaan.snorql.extensions.performance.models.GeoReplicaLagResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

/**
 * Class which implements the ReadReplicationLag metric
 *
 * Contains a row for each replication link between primary and secondary databases in a geo-replication partnership
 *
 * @constructor Create ReadReplicationLag metric
 */
class GeoReplicaLagMetric :
    IMetric<GeoReplicaLagInput, GeoReplicaLagResult, GeoReplicaLagRecommendation> {

    companion object {
        private const val OK_REPLICATION_LAG_THRESHOLD = 120
    }

    override fun getMetricResult(
        metricInput: GeoReplicaLagInput,
        metricConfig: MetricConfig
    ): GeoReplicaLagResult {
        // check the metricConfig.supportedHistory before getting the query
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException(
                    "SQL config query [main] not found under " +
                            "config [${metricInput.metricId}]"
                )

        val response = SqlMetricManager.queryExecutor
            .execute<GeoReplicaLagDTO>(metricInput.primaryDatabaseName, query)
        return GeoReplicaLagResult(response)
    }

    override fun getMetricResponseMetadata(
        metricInput: GeoReplicaLagInput,
        metricOutput: MetricOutput<GeoReplicaLagResult, GeoReplicaLagRecommendation>
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
        metricInput: GeoReplicaLagInput,
        metricResult: GeoReplicaLagResult
    ): GeoReplicaLagRecommendation? {
        if (metricInput.recommendationRequired) {
            val geoReplicaLagDTO = metricResult.queryList[0]
            return if (geoReplicaLagDTO.replicationLagSec <= OK_REPLICATION_LAG_THRESHOLD) {
                GeoReplicaLagRecommendation(
                    "No significant replication lag. No action required.",
                    false
                )
            } else {
                GeoReplicaLagRecommendation(
                    "Replication lag is more than 120s. Please check any long running queries which might cause this.",
                    false
                )
            }
        }
        return null
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
