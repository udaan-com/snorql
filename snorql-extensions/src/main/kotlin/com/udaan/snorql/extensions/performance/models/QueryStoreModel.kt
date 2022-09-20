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
 * Wrapper class to hold individual attributes for QueryStore Metric
 *
 * @property queryId
 * @property objectId
 * @property objectName
 * @property querySqlText
 * @property totalDuration
 * @property totalCpuTime
 * @property totalLogicalIoReads
 * @property totalLogicalIoWrites
 * @property totalPhysicalIoReads
 * @property totalClrTime
 * @property totalDop
 * @property totalQueryMaxUsedMemory
 * @property totalRowcount
 * @property totalLogBytesUsed
 * @property totalTempdbSpaceUsed
 * @property avgDuration
 * @property avgCpuTime
 * @property avgLogicalIoReads
 * @property avgLogicalIoWrites
 * @property avgPhysicalIoReads
 * @property avgClrTime
 * @property avgDop
 * @property avgQueryMaxUsedMemory
 * @property avgRowcount
 * @property avgLogBytesUsed
 * @property avgTempdbSpaceUsed
 * @property avgQueryWaitTime
 * @property countExecutions
 * @property numPlans
 * @constructor Create QueryStore wrapper
 */

data class QueryStoreDTO(
    @ColumnName("query_id")
    val queryId: Int,
    @ColumnName("object_id")
    val objectId: Int,
    @ColumnName("object_name")
    val objectName: String?,
    @ColumnName("query_sql_text")
    val querySqlText: String,
    @ColumnName("count_executions")
    val countExecutions: Int,
    @ColumnName("num_plans")
    val numPlans: Int,

    @ColumnName("total_duration")
    val totalDuration: Float,
    @ColumnName("total_cpu_time")
    val totalCpuTime: Float,
    @ColumnName("total_logical_io_reads")
    val totalLogicalIoReads: Int,
    @ColumnName("total_logical_io_writes")
    val totalLogicalIoWrites: Int,
    @ColumnName("total_physical_io_reads")
    val totalPhysicalIoReads: Int,
    @ColumnName("total_clr_time")
    val totalClrTime: Int,
    @ColumnName("total_dop")
    val totalDop: Int,
    @ColumnName("total_query_max_used_memory")
    val totalQueryMaxUsedMemory: Float,
    @ColumnName("total_rowcount")
    val totalRowcount: Int,
    @ColumnName("total_log_bytes_used")
    val totalLogBytesUsed: Float,
    @ColumnName("total_tempdb_space_used")
    val totalTempdbSpaceUsed: Float,

    @ColumnName("avg_duration")
    val avgDuration: Float,
    @ColumnName("avg_cpu_time")
    val avgCpuTime: Float,
    @ColumnName("avg_logical_io_reads")
    val avgLogicalIoReads: Int,
    @ColumnName("avg_logical_io_writes")
    val avgLogicalIoWrites: Int,
    @ColumnName("avg_physical_io_reads")
    val avgPhysicalIoReads: Int,
    @ColumnName("avg_clr_time")
    val avgClrTime: Int,
    @ColumnName("avg_dop")
    val avgDop: Int,
    @ColumnName("avg_query_max_used_memory")
    val avgQueryMaxUsedMemory: Float,
    @ColumnName("avg_rowcount")
    val avgRowcount: Int,
    @ColumnName("avg_log_bytes_used")
    val avgLogBytesUsed: Float,
    @ColumnName("avg_tempdb_space_used")
    val avgTempdbSpaceUsed: Float,
    @ColumnName("avg_query_wait_time")
    val avgQueryWaitTime: Float
)

/**
 * Wrapper class to hold QueryStore Metric input
 *
 * @property metricId id of QueryStoreMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @property startTime start time
 * @property endTime end time
 * @constructor Create QueryStoreInput metric input
 */
data class QueryStoreInput(
    override val metricId: String = PerformanceEnums.QUERY_STORE.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val startTime:String,
    val endTime:String,
    val sortKey: String
) : MetricInput()

/**
 * Wrapper class to hold Query Store metric result
 *
 * <p>Result of query store is a list of metrics
 * which are wrapped using [QueryStoreDTO]</p>
 *
 * @property queryList list of metrics wrapped in [QueryStoreDTO]
 * @constructor Create empty query store result
 */
data class QueryStoreResult(val queryList: List<QueryStoreDTO>) : IMetricResult()