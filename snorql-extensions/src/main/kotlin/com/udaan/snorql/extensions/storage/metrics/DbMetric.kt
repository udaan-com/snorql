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

import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.extensions.storage.models.DbResult
import com.udaan.snorql.extensions.storage.models.DbStorageSize
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class DbMetric :
    IMetric<DbInput, DbResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: DbInput,
        metricConfig: MetricConfig
    ): DbResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

        val result = executeQuery<DbDTO>(
            metricInput.databaseName,
            query
        ) // SqlMetricManager.queryExecutor.execute<DbDTO>(metricInput.databaseName, query)
        val dbSizeQuery =
            metricConfig.queries["dbSize"]
                ?: throw SQLMonitoringConfigException("SQL config query [dbSize] not found under config [${metricInput.metricId}]")

        val paramMap = mapOf("databaseName" to metricInput.dbName)
        val dbSizeResult = executeQuery<Int>(
            metricInput.databaseName,
            dbSizeQuery,
            paramMap
        ) // SqlMetricManager.queryExecutor.execute<Int>(metricInput.databaseName, dbSizeQuery, paramMap)
        val dbResultList: List<DbStorageSize> = result.mapIndexed { index, it ->
            DbStorageSize(
                databaseName = it.databaseName,
                databaseSize = it.databaseSize,
                unallocatedSpace = it.unallocatedSpace,
                reserved = it.reserved,
                data = it.data,
                indexSize = it.indexSize,
                unused = it.unused,
                dbTotalSize = dbSizeResult[index]
            )
        }
        return DbResult(dbResultList)
    }

    override fun getMetricResponseMetadata(
        metricInput: DbInput,
        metricOutput: MetricOutput<DbResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        val dbSizeQuery = metricConfig.queries["dbSize"]
        responseMetadata["underlyingQueries"] = listOf(query, dbSizeQuery)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description
        return responseMetadata
    }


    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }

    inline fun <reified T> executeQuery(
        databaseName: String,
        queryString: String,
        params: Map<String, *> = mapOf<String, Any>()
    ): List<T> {
        return SqlMetricManager.queryExecutor.execute<T>(databaseName = databaseName, query = queryString)
    }
}