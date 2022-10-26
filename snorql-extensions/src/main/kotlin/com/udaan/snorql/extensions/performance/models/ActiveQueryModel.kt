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
 * Wrapper class to hold individual active query for Active Query Metric
 *
 * @property sessionId session id for the active query
 * @property status status of the active query
 * @property blockedBy query blocker (if query is blocked)
 * @property waitType type of wait (if query is in waiting state)
 * @property waitResource resource for which wuery is waiting (if query is in waiting state)
 * @property percentComplete percentage of query execution completed
 * @property waitTime time for which the query has been waiting
 * @property cpuTime cpu time utilized by the query
 * @property logicalReads logical reads performed by the query
 * @property reads reads operations performed by the query
 * @property writes write operations performed by the query
 * @property elapsedTime time elapsed since query execution started
 * @property queryText actual active query string
 * @property storedProc stored procedure
 * @property command
 * @property loginName
 * @property hostName name of host on which query is getting executed
 * @property programName program name on which query is running
 * @property hostProcessId host's process id
 * @property lastRequestEndTime last request end time
 * @property loginTime login time
 * @property openTransactionCount count of transactions currently open
 * @constructor Create empty Active query d t o
 */
data class ActiveQueryDTO(
    val sessionId: Int,
    val status: String,
    val blockedBy: Int,
    val waitType: String?,
    val waitResource: String?,
    val percentComplete: Int,
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
 * Wrapper class for Active query metric input
 *
 * @property metricId id of ActiveQueryMetric
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create Active query input
 */
data class ActiveQueryInput(
    override val metricId: String = PerformanceEnums.ACTIVE_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()

/**
 * Wrapper class for Active query metric result
 *
 * Result of active queries metric is a list of Active Queries with metadata
 * which are wrapped using [ActiveQueryDTO]
 *
 * @property queryList list of active queries wrapped in [ActiveQueryDTO]
 * @constructor Create Active query result
 */
data class ActiveQueryResult(val queryList: List<ActiveQueryDTO>) : IMetricResult()
