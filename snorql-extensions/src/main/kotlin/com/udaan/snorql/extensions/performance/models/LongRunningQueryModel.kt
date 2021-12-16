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
 * Long running query d t o
 *
 * @property sessionId
 * @property status
 * @property blockedBy
 * @property waitType
 * @property waitResource
 * @property waitTime
 * @property cpuTime
 * @property logicalReads
 * @property reads
 * @property writes
 * @property elapsedTime
 * @property queryText
 * @property storedProc
 * @property command
 * @property loginName
 * @property hostName
 * @property programName
 * @property hostProcessId
 * @property lastRequestEndTime
 * @property loginTime
 * @property openTransactionCount
 * @constructor Create empty Long running query d t o
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
 * Long running input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @property elapsedTime
 * @constructor Create empty Long running input
 */
data class LongRunningInput(
    override val metricId: String = PerformanceEnums.LONG_RUNNING_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val elapsedTime: String
) : MetricInput()

/**
 * Long running result
 *
 * @property queryList
 * @constructor Create empty Long running result
 */
data class LongRunningResult(val queryList: List<LongRunningQueryDTO>) : IMetricResult()