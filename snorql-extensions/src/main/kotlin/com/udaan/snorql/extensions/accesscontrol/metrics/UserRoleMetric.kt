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


import com.udaan.snorql.extensions.accesscontrol.models.UserRoleDTO
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleInput
import com.udaan.snorql.extensions.accesscontrol.models.UserRoleResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

/**
 * User role metric class is the implementation of user role metric
 *
 * The user role metric can be used to get the user roles defined for the database.
 * User role metric returns the name, privileges and type of user role.
 *
 * @constructor Create user role metric
 */
class UserRoleMetric :
    IMetric<UserRoleInput, UserRoleResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: UserRoleInput,
        metricConfig: MetricConfig
    ): UserRoleResult {
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
