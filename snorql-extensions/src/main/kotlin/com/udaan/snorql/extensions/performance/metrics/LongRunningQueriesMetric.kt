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

import com.udaan.snorql.extensions.MetricQueryExecutor
import com.udaan.snorql.extensions.performance.models.*
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.models.*

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

        val result = MetricQueryExecutor.metricQueryExecutor<LongRunningQueryDTO>(metricInput,query, metricConfig)
        return LongRunningResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: LongRunningInput,
        metricOutput: MetricOutput<LongRunningResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val query =
            getMetricConfig(metricInput.metricId).queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        return responseMetadata
    }


    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}