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

package com.udaan.snorql.framework.metric

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.udaan.snorql.framework.SQLMonitoringConnectionException
import com.udaan.snorql.framework.SQLMonitoringException
import com.udaan.snorql.framework.models.*

object SqlMetricManager {

    private var connection: Connection? = null

    private const val CONFIG_FILE_LOCATION = "/sql-monitoring-conf.json"

    private val metricIdToMetricMap: MutableMap<String, Any> = mutableMapOf()

    val logger by logger()

    private val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false).registerKotlinModule()
        }
    val configuration: Configuration by lazy {
        val file = SqlMetricManager::class.java.getResource(CONFIG_FILE_LOCATION).readText()
        if (file.isNotEmpty()) {
            val typeRef = object : TypeReference<Map<String, MetricConfig>>() {}
            Configuration(objectMapper.readValue(file, typeRef))
        } else {
            logger.warn("SQL monitoring configuration file [$CONFIG_FILE_LOCATION] not found in classpath")
            Configuration(mapOf())
        }
    }

    fun setConnection(connection: Connection) {
        SqlMetricManager.connection = connection
    }

    val queryExecutor: QueryExecutor
        get() {
            return connection?.let { QueryExecutor(it) }
                    ?: throw SQLMonitoringConnectionException("Connection is null. Cannot get QueryExecutor instance.")
        }

    fun addMetric(metricId: String,
            instance: IMetric<*, *, *>
    ) {
        metricIdToMetricMap[metricId] = instance
    }

    fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> getMetric(metricId: String,
                                                                                  metricInput: T): MetricResponse<*, *> {
        val instance =
            metricIdToMetricMap[metricId]?.let { it as IMetric<T, O, V> }
                    ?: throw SQLMonitoringException("IMetric impl instance not found for metric id [$metricId]")
        return instance.getMetricResponse(metricInput)
    }

}