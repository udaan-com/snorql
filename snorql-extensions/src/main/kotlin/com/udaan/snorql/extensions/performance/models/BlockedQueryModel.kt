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
 * Blocked queries d t o
 *
 * @property sessionId
 * @property blockedBy
 * @property blockingThese
 * @property batchText
 * @property inputBuffer
 * @property loginName
 * @property status
 * @property waitType
 * @property waitResource
 * @property waitTime
 * @property cpuTime
 * @property logicalReads
 * @property reads
 * @property writes
 * @property hostName
 * @property programName
 * @property hostProcessId
 * @property loginTime
 * @property blockingTree
 * @property lastRequestEndTime
 * @property openTransactionCount
 * @property command
 * @property elapsedTime
 * @property queryText
 * @property storedProc
 * @constructor Create empty Blocked queries d t o
 */
data class BlockedQueriesDTO(
    val sessionId: Int,
    val blockedBy: Int?,
    val blockingThese: String?,
    val batchText:String?,
    val inputBuffer:String?,
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
 * Blocked queries input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @constructor Create empty Blocked queries input
 */
data class BlockedQueriesInput(
    override val metricId: String = PerformanceEnums.BLOCKED_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String

) : MetricInput()

/**
 * Blocked queries result
 *
 * @property queryList
 * @constructor Create empty Blocked queries result
 */
data class BlockedQueriesResult(val queryList: List<BlockedQueriesDTO>) : IMetricResult()