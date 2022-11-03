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

package com.udaan.snorql.extensions.session.metrics

import com.udaan.snorql.extensions.session.models.SessionLocksDTO
import com.udaan.snorql.extensions.session.models.SessionLocksInput
import com.udaan.snorql.extensions.session.models.SessionLocksResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class SessionLocksMetric : IMetric<SessionLocksInput, SessionLocksResult, IMetricRecommendation> {

    override fun getMetricResult(metricInput: SessionLocksInput, metricConfig: MetricConfig): SessionLocksResult {
        val query = metricConfig.queries["main"]
            ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config " +
                    "[${metricInput.metricId}]")
        val paramMap = mapOf("sessionIdParam" to metricInput.sessionId)
        val result = SqlMetricManager.queryExecutor.execute<SessionLocksDTO>(
            databaseName = metricInput.databaseName,
            query = query,
            params = paramMap
        )
        return SessionLocksResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: SessionLocksInput,
        metricOutput: MetricOutput<SessionLocksResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description
        responseMetadata["supportsHistorical"] = metricConfig.supportsHistorical
        responseMetadata["minimumRepeatInterval"] = metricConfig.persistDataOptions?.get("minimumRepeatInterval") ?: ""
        return responseMetadata
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
