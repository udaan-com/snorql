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

import com.udaan.snorql.extensions.storage.models.TableUnusedIndexDTO
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexInput
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexRecommendation
import com.udaan.snorql.extensions.storage.models.TableUnusedIndexResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class TableUnusedIndexMetric :
    IMetric<TableUnusedIndexInput, TableUnusedIndexResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: TableUnusedIndexInput,
        metricConfig: MetricConfig
    ): TableUnusedIndexResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        val paramMap = mapOf("tableName" to metricInput.tableName) // adding the params here
        val result = SqlMetricManager.queryExecutor.execute<TableUnusedIndexDTO>(metricInput.databaseName, query, paramMap)
        return TableUnusedIndexResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: TableUnusedIndexInput,
        metricOutput: MetricOutput<TableUnusedIndexResult, IMetricRecommendation>
    ): Map<String, Any> {
        val responseMetadata = mutableMapOf<String, Any>()
        val query =
            getMetricConfig(metricInput.metricId).queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        return responseMetadata
    }

    override fun getMetricRecommendations(
        metricInput: TableUnusedIndexInput,
        metricResult: TableUnusedIndexResult
    ): TableUnusedIndexRecommendation? {
        if(metricInput.recommendationRequired) {
            val indexListToBeDropped: List<String> = metricResult.queryList
                .filter { it.userSeeks == 0 && it.userLookups == 0 && it.userScans == 0 }
                .map { it.indexName }
            return if (indexListToBeDropped.isEmpty()) TableUnusedIndexRecommendation(listOf("No action required."))
            else TableUnusedIndexRecommendation(listOf("Drop indexes: ${indexListToBeDropped.joinToString(",")} to save storage."))
        }
        return null
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
