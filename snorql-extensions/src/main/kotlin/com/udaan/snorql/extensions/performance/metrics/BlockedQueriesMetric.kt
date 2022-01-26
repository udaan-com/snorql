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
 * Class which implements Blocked Queries Metric
 *
 * <p>Blocked queries metric can be used to fetch the queries which are in the
 * blocked state.</p>
 *
 * @constructor Create Blocked queries metric
 */
class BlockedQueriesMetric :
    IMetric<BlockedQueriesInput, BlockedQueriesResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: BlockedQueriesInput,
        metricConfig: MetricConfig
    ): BlockedQueriesResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")
        val results = SqlMetricManager.queryExecutor.execute<BlockedQueriesDTO>(metricInput.databaseName, query)

        for(result in results) {
            if(result.blockedBy != 0){
                generateBlockingTree(result, results)
            }
        }
        val treeResults = results.filter { it.blockedBy == 0 }
        return BlockedQueriesResult(treeResults)
    }

    override fun getMetricResponseMetadata(
        metricInput: BlockedQueriesInput,
        metricOutput: MetricOutput<BlockedQueriesResult, IMetricRecommendation>
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

    private fun generateBlockingTree(result:BlockedQueriesDTO, results:List<BlockedQueriesDTO>): BlockedQueriesDTO?{
        if (result.blockedBy != 0){
            if (!results.single { it.sessionId == result.blockedBy }.blockingTree.any{ it?.sessionId == result.sessionId})
            {
                results.single { it.sessionId == result.blockedBy }.blockingTree.add(result)
            }
            generateBlockingTree(results.single { it.sessionId == result.blockedBy }, results)
        }
        return result
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}