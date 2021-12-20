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

/**
 * Model class to hold individual active DDL query for Active DDL Query Metric
 * Model class to hold the query result of [ActiveDDLMetric]
 * @property currentStep current active step in the query
 * @property queryText Active DDL SQL query
 * @property totalRows Rows affected by active DDL query
 * @property rowsProcessed Number of rows processed by the query
 * @property rowsLeft Number of rows remaining to be processed by query
 * @property percentComplete execution completion percentage of the DDL query
 * @property elapsedSeconds seconds elapsed since execution started
 * @property estimatedSecondsLeft seconds left to complete execution
 * @property estimatedCompletionTime estimated time for complete execution of query
 * @constructor Create ActiveDDLDTO for an active DDL query
 */
data class ActiveDDLDTO(
    val currentStep: String?,
    val queryText: String,
    val totalRows: Int?,
    val rowsProcessed: Int?,
    val rowsLeft: Int?,
    val percentComplete: Float?,
    val elapsedSeconds: Int?,
    val estimatedSecondsLeft: Int?,
    val estimatedCompletionTime: String?
)

/**
 * Model class to hold input for Active DDL Queries metric
 *
 * @property metricId id of ActiveDDLMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create ActiveDDLMetric Input
 */
data class ActiveDDLInput(
    override val metricId: String = PerformanceEnums.ACTIVE_DDL.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
) : MetricInput()

/**
 * Model class to hold the result of ActiveDDLMetric
 *
 * Result of active DDL queries metric is a list of Active DDL Queries with metadata
 * which is wrapped using [ActiveDDLResult]
 *
 * @property queryList List of active DDL queries wrapped in [ActiveDDLDTO]
 * @constructor Create ActiveDDLMetric Result
 */
data class ActiveDDLResult(val queryList: List<ActiveDDLDTO>) : IMetricResult()