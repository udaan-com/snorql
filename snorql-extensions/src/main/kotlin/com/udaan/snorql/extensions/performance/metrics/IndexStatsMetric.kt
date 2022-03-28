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
 * Class which implements the Database Index Statistics metric
 *
 * The index stats metric can be used to obtain statistics for all
 * indexes defined for each table in a database.
 *
 * @constructor Create Index stats metric
 */
class IndexStatsMetric :
    IMetric<IndexStatInput, IndexStatResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: IndexStatInput,
        metricConfig: MetricConfig
    ): IndexStatResult {
        // check the metricConfig.supportedHistory before getting the query
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        // adding the stat params here
        val paramMap = mapOf("tableName" to metricInput.tableName, "indexName" to metricInput.indexName)

        val result = SqlMetricManager.queryExecutor.execute<IndexStatDTO>(metricInput.databaseName, query, paramMap)
            return IndexStatResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: IndexStatInput,
        metricOutput: MetricOutput<IndexStatResult, IMetricRecommendation>
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