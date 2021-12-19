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

package com.udaan.snorql.extensions.storage.metrics

import com.udaan.snorql.extensions.storage.models.PVSDTO
import com.udaan.snorql.extensions.storage.models.PVSInput
import com.udaan.snorql.extensions.storage.models.PVSResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

/**
 * Class implementing the Persistent Version Store Metric
 *
 * The persistent version store metric fetches the persistent version store size with open transactions statistics.
 *
 * @constructor Create Persistent version store metric
 */
class PVSMetric :
    IMetric<PVSInput, PVSResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: PVSInput,
        metricConfig: MetricConfig
    ): PVSResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        val result = SqlMetricManager.queryExecutor.execute<PVSDTO>(metricInput.databaseName, query)
        return PVSResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: PVSInput,
        metricOutput: MetricOutput<PVSResult, IMetricRecommendation>
    ): Map<String, Any> {
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
