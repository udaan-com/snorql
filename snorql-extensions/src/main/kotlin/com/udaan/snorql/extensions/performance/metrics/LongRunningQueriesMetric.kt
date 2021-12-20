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

import com.udaan.snorql.extensions.performance.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

/**
 * Class which implements the Long Running Queries metric
 *
 * <p>The long running queries metric is used to list the queries
 * with useful metadata which have been running for more than a
 * specified time (seconds).</p>
 *
 * @constructor Create Long running queries metric
 */
class LongRunningQueriesMetric :
    IMetric<LongRunningInput, LongRunningResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: LongRunningInput,
        metricConfig: MetricConfig
    ): LongRunningResult {
        // check the metricConfig.supportedHistory before getting the query
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")
        val paramMap = mapOf("elapsedTimeParam" to metricInput.elapsedTime)
        val result = SqlMetricManager.queryExecutor.execute<LongRunningQueryDTO>(metricInput.databaseName, query,paramMap)
        return LongRunningResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: LongRunningInput,
        metricOutput: MetricOutput<LongRunningResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description

        return responseMetadata
    }


    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}