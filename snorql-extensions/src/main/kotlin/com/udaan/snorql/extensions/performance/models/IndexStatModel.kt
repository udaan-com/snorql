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

class IndexStatModel {
}
/**
 * Data class for Data transfer object of index stats
 * @param [name]
 * @param [updated]
 * @param [rows]
 * @param [rowsSampled]
 * @param [steps]
 * @param [density]
 * @param [averageKeyLength]
 * @param [stringIndex]
 * @param [filterExpression]
 * @param [unfilteredRows]
 * @param [persistedSamplePercent]
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

data class IndexStatInput(
    override val metricId: String = PerformanceEnums.INDEX_STATS.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val tableName:String, val indexName:String
) : MetricInput()

data class IndexStatResult(val queryList: List<IndexStatDTO>) : IMetricResult()