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
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName


/**
 * Model class to hold individual index statistics for Index Stats Metric
 *
 * @property name name of the index
 * @property updated timestamp when index was last updated
 * @property rows number of rows
 * @property rowsSampled number of rows sampled
 * @property steps number of steps
 * @property density index density
 * @property averageKeyLength average length of keys in index
 * @property stringIndex is it string type index
 * @property filterExpression filter expression
 * @property unfilteredRows number of rows unfiltered
 * @property persistedSamplePercent percentage of persisted samples
 * @constructor Create Index Statistics model
 */

data class IndexStatDTO(
    val name: String?,
    val updated: String?,
    val rows: Int?,

    @ColumnName("Rows Sampled")
    val rowsSampled: Int?,

    val steps: Int?,
    val density: Int?,

    @ColumnName("Average key length")
    val averageKeyLength: Float?,

    @ColumnName("String Index")
    val stringIndex: String?,

    @ColumnName("Filter Expression")
    val filterExpression: String?,

    @ColumnName("Unfiltered Rows")
    val unfilteredRows: Int?,

    @ColumnName("Persisted Sample Percent")
    val persistedSamplePercent: Int?
)

/**
 * Model class to hold Index Stats Metric input
 *
 * @property metricId id of IndexStatsMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @property tableName table on which metric is used
 * @property indexName name of the index
 * @constructor Create Index stat metric input model
 */
data class IndexStatInput(
    override val metricId: String = PerformanceEnums.INDEX_STATS.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val tableName:String, val indexName:String
) : MetricInput()

/**
 * Model class to hold Index stat metric result
 *
 * Result of index stats metric is a list of Index Statistics
 * which are wrapped using [IndexStatDTO]
 *
 * @property queryList list of index statistics wrapped in [IndexStatDTO]
 * @constructor Create empty Index stat result model
 */
data class IndexStatResult(val queryList: List<IndexStatDTO>) : IMetricResult()