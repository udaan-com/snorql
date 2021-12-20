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


/**
 * Abstract class to define the desired metric input
 *
 * @constructor Create empty Metric input
 */
abstract class MetricInput {
    abstract val metricId: String
    abstract val metricPeriod: MetricPeriod
    abstract val databaseName: String
    val from: Timestamp? = null
    val to: Timestamp? = null
    val recommendationRequired: Boolean = false
}

/**
 * Metric config
 *
 * @property queries
 * @property supportsHistorical
 * @property supportsRealTime
 * @property isParameterized
 * @constructor Create empty Metric config
 */
data class MetricConfig(val queries: Map<String, String>,
                        val supportsHistorical: Boolean,
                        val supportsRealTime: Boolean,
                        val isParameterized: Boolean)

/**
 * Metric output
 *
 * @param T
 * @param V
 * @property result
 * @property recommendation
 * @constructor Create empty Metric output
 */
data class MetricOutput<T : IMetricResult, V : IMetricRecommendation>(val result: T,
                                                                      val recommendation: V?)

/**
 * Data class to hold the metric response
 * MetricResponse holds the following:
 * <ul>
 * <li>MetricInput</li>
 * <li>MetricOutput</li>
 * <li>Metadata</li>
 * <ul>
 *
 * @param T                 The wrapper DTO class for metric result
 * @param V                 The wrapper DTO class for metric recommendation
 * @property metricInput    The input as received in the request
 *                          from the user
 * @property metricOutput   The output to be returned to the user
 *                          The output is wrapped in a metric DTO
 *                          class and recommendation along with it
 *                          is wrapped in metric recommendation DTO
 *                          class
 * @property metadata       Any additional metadata that is to be
 *                          sent back to the user
 * @constructor             Create a metric response instance with
 *                          desired metric input, metric output and
 *                          metadata
 */
data class MetricResponse<T : IMetricResult, V : IMetricRecommendation>(
    val metricInput: MetricInput,
    val metricOutput: MetricOutput<T, V>,
    val metadata: Map<String, Any>? = null
)

/**
 * I metric result
 *
 * @constructor Create empty I metric result
 */
abstract class IMetricResult

/**
 * I metric recommendation
 *
 * @constructor Create empty I metric recommendation
 */
abstract class IMetricRecommendation
