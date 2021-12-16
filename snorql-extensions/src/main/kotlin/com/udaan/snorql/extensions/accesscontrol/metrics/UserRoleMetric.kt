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

package com.udaan.snorql.extensions.accesscontrol.metrics


import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*
import com.udaan.snorql.extensions.accesscontrol.models.*

/**
 * User role metric
 *
 * @constructor Create empty User role metric
 */
class UserRoleMetric :
    IMetric<UserRoleInput, UserRoleResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: UserRoleInput,
        metricConfig: MetricConfig
    ): UserRoleResult {
        // check the metricConfig.supportedHistory before getting the query
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")
        val result = SqlMetricManager.queryExecutor.execute<UserRoleDTO>(metricInput.databaseName, query)


        return UserRoleResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: UserRoleInput,
        metricOutput: MetricOutput<UserRoleResult, IMetricRecommendation>
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