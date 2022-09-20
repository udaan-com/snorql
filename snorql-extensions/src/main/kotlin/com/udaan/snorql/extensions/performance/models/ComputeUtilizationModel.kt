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
 * Model class to hold records for avg,max CPU, I/O, and memory consumption for ComputeUtilization
 * @property timeId UTC time indicates the end of the current reporting interval.
 * @property maxCpuPercent Max compute utilization in percentage of the limit of the service tier.
 * @property maxDataIoPercent Max data I/O utilization in percentage of the limit of the service tier.
 * @property maxLogIoPercent Max transaction log writes (in MBps) as percentage of the service tier limit.
 * @property maxMemoryPercent Max memory usage.
 * @property avgCpuPercent Average compute utilization in percentage of the limit of the service tier.
 * @property avgDataIoPercent Average data I/O utilization in percentage of the limit of the service tier.
 * @property avgLogIoPercent Average transaction log writes (in MBps) as percentage of the service tier limit.
 * @property avgMemoryPercent Average memory usage.
 * @constructor Create ComputeUtilizationDTO
 */
data class ComputeUtilizationDTO(
    @ColumnName("time_id")
    val timeId: String,
    @ColumnName("max_cpu_percent")
    val maxCpuPercent: Float,
    @ColumnName("max_data_io_percent")
    val maxDataIoPercent: Float,
    @ColumnName("max_log_io_percent")
    val maxLogIoPercent: Float,
    @ColumnName("max_memory_percent")
    val maxMemoryPercent: Float,
    @ColumnName("avg_cpu_percent")
    val avgCpuPercent: Float,
    @ColumnName("avg_data_io_percent")
    val avgDataIoPercent: Float,
    @ColumnName("avg_log_io_percent")
    val avgLogIoPercent: Float,
    @ColumnName("avg_memory_percent")
    val avgMemoryPercent: Float
)

/**
 * Wrapper class to hold input for ComputeUtilization metric
 *
 * @property metricId id of ComputeUtilizationMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create empty ComputeUtilization input
 */
data class ComputeUtilizationInput(
    override val metricId: String = PerformanceEnums.COMPUTE_UTILIZATION.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()

/**
 * Wrapper class to hold the result of ComputeUtilization
 *
 * <p>Result of ComputeUtilization metric is a list of avg cpu, data-io, log-write & memory usage metrics with metadata
 * which is wrapped using [ComputeUtilizationResult]</p>
 *
 * @property queryList List of active DDL queries wrapped in [ComputeUtilizationDTO]
 * @constructor Create ComputeUtilizationMetric Result
 */
data class ComputeUtilizationResult(val queryList: List<ComputeUtilizationDTO>) : IMetricResult()