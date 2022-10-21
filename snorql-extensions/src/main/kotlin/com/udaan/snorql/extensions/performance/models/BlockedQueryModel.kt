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
 * Wrapper class to hold blocked query with metadata for Blocked Query metric
 *
 * @property sessionId session id of the query
 * @property blockedBy query blocker (if query is blocked)
 * @property blockingThese queries being blocked by the active query
 * @property batchText
 * @property inputBuffer
 * @property loginName
 * @property status status of the active query
 * @property waitType type of wait (if query is in waiting state)
 * @property waitResource resource for which query is waiting (if query is in waiting state)
 * @property waitTime time for which the query has been waiting
 * @property cpuTime cpu time utilized by the query
 * @property logicalReads logical reads performed by the query
 * @property reads read operations performed by the query
 * @property writes write operations performed by the query
 * @property hostName name of host on which query is getting executed
 * @property programName program name on which query is running
 * @property hostProcessId id of query process on host
 * @property loginTime login time
 * @property blockingTree hierarchical representation of queries blocked by active query
 * @property lastRequestEndTime last request end time
 * @property openTransactionCount number of transactions open by active query
 * @property command command
 * @property elapsedTime time elapsed since execution of active query started
 * @property queryText actual active query string
 * @property storedProc stored procedure
 * @constructor Create Blocked query wrapper
 */
data class BlockedQueriesDTO(
    val sessionId: Int,
    val blockedBy: Int?,
    val blockingThese: String?,
    val batchText: String?,
    val inputBuffer: String?,
    val loginName: String?,
    val status: String,
    val waitType: String?,
    val waitResource: String?,
    val waitTime: String?,
    val cpuTime: Int?,
    val logicalReads: Int?,
    val reads: Int?,
    val writes: Int?,
    val hostName: String?,
    val programName: String?,
    val hostProcessId: Int,
    val loginTime: String,
    var blockingTree: MutableList<BlockedQueriesDTO?> = mutableListOf(),
    val lastRequestEndTime: String?,
    val openTransactionCount: Int,
    val command: String?,
    val elapsedTime: String?,
    val queryText: String?,
    val storedProc: String?
)

/**
 * Wrapper to hold blocked queries metric input
 *
 * @property metricId id of blocked queries metric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create Blocked queries metric input
 */
data class BlockedQueriesInput(
    override val metricId: String = PerformanceEnums.BLOCKED_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String

) : MetricInput()

/**
 * Wrapper class for Blocked queries metric result
 *
 * Result of blocked queries metric is a list of Blocked Queries with metadata
 * which are wrapped using [BlockedQueriesDTO]
 *
 * @property queryList list of bloacked queries wrapped in [BlockedQueriesDTO]
 * @constructor Create Blocked queries metric result
 */
data class BlockedQueriesResult(val queryList: List<BlockedQueriesDTO>) : IMetricResult()
