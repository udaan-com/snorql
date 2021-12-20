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

package com.udaan.snorql.framework.models

import java.sql.Timestamp


abstract class MetricInput {
    abstract val metricId: String
    abstract val metricPeriod: MetricPeriod
    abstract val databaseName: String
    val from: Timestamp? = null
    val to: Timestamp? = null
    val recommendationRequired: Boolean = false
}

data class MetricConfig(val queries: Map<String, String>,
        val supportsHistorical: Boolean,
        val supportsRealTime: Boolean,
        val isParameterized: Boolean,
        val referenceDoc: String,
        val description: String)

data class MetricOutput<T : IMetricResult, V : IMetricRecommendation>(val result: T,
                                                                      val recommendation: V?)

data class MetricResponse<T : IMetricResult, V : IMetricRecommendation>(
    val metricInput: MetricInput,
    val metricOutput: MetricOutput<T, V>,
    val metadata: Map<String, Any>? = null
)

abstract class IMetricResult
abstract class IMetricRecommendation
