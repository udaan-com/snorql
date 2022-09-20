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

package com.udaan.snorql.extensions.performance.models

import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName


/**
 * Wrapper class to hold individual attributes for Log Space Metric
 *
 * @property name
 * @property spaceUsedInGB
 * @property maxSpaceInGB
 * @constructor Create LogSpaceUsage wrapper
 */

data class LogSpaceUsageDTO(
    val name: String,
    val spaceUsedInGB: Double,
    val maxSpaceInGB: Double
)

/**
 * Wrapper class to hold LogSpaceUsage Metric input
 *
 * @property metricId id of LogSpaceUsageMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create LogSpaceUsageInput metric input
 */
data class LogSpaceUsageInput(
    override val metricId: String = PerformanceEnums.LOG_SPACE_USAGE.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String
) : MetricInput()

/**
 * Wrapper class to hold Log Space metric result
 *
 * <p>Result of query store is a list of metrics
 * which are wrapped using [LogSpaceUsageDTO]</p>
 *
 * @property queryList list of metrics wrapped in [LogSpaceUsageDTO]
 * @constructor Create empty query store result
 */
data class LogSpaceUsageResult(val queryList: List<LogSpaceUsageDTO>) : IMetricResult()

/**
 * Model class to hold recommendations for [LogSpaceUsageMetric]
 *
 * @property text recommendation text
 * @constructor Create Log Space Usage metric recommendation model
 */
data class LogSpaceUsageRecommendation(val text: String, val isActionRequired: Boolean) : IMetricRecommendation()