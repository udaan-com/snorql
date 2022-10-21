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
 * Wrapper class to hold Long Running Query with metadata for Long Running Queries metric
 *
 * @property sessionId session id of the query
 * @property status current status of the query
 * @property blockedBy query blocker (if query is blocked)
 * @property waitType type of wait (if query is in waiting state)
 * @property waitResource resource for which query is waiting (if query is in waiting state)
 * @property waitTime time for which the query has been waiting
 * @property cpuTime cpu time utilized by the query
 * @property logicalReads logical reads performed by the query
 * @property reads read operations performed by the query
 * @property writes write operations performed by the query
 * @property elapsedTime time elapsed since execution of long running query started
 * @property queryText actual long running query string
 * @property storedProc stored procedure
 * @property command command
 * @property loginName login name
 * @property hostName name of host on which query is getting executed
 * @property programName program name on which query is running
 * @property hostProcessId id of query process on host
 * @property lastRequestEndTime last request end time
 * @property loginTime login time
 * @property openTransactionCount number of transactions open by long running query
 * @constructor Create Long running query metric wrapper
 */
data class LongRunningQueryDTO(
    val sessionId: Int,
    val status: String,
    val blockedBy: Int,
    val waitType: String?,
    val waitResource: String?,
    val waitTime: String?,
    val cpuTime: Int?,
    val logicalReads: Int?,
    val reads: Int?,
    val writes: Int?,
    val elapsedTime: String,
    val queryText: String,
    val storedProc: String,
    val command: String,
    val loginName: String,
    val hostName: String,
    val programName: String,
    val hostProcessId: Int,
    val lastRequestEndTime: String,
    val loginTime: String,
    val openTransactionCount: Int
)

/**
 * Wrapper class to hold Long running query metric input
 *
 * @property metricId id of Long running query metric
 * @property metricPeriod metric period
 * @property databaseName database on which metric is used
 * @property elapsedTime filter queries whose time elapsed is greater than [elapsedTime]
 * @constructor Create empty running query metric input
 */
data class LongRunningInput(
    override val metricId: String = PerformanceEnums.LONG_RUNNING_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val elapsedTime: String
) : MetricInput()

/**
 * Wrapper class for Long running queries metric result
 *
 * Result of long-running queries metric is a list of Long Running Queries with metadata
 * which are wrapped using [LongRunningQueryDTO]
 *
 * @property queryList list of long running queries wrapped in [LongRunningQueryDTO]
 * @constructor Create empty Long running result
 */
data class LongRunningResult(val queryList: List<LongRunningQueryDTO>) : IMetricResult()
