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


class BlockedQueryModel{

}
/**
 * Data class for Data transfer object of azure_databases_mapping
 * @param [sessionId]
 * @param [status]
 * @param [blockedBy]
 * @param [waitType]
 * @param [waitResource]
 * @param [waitTime]
 * @param [cpuTime]
 * @param [logicalReads]
 * @param [reads]
 * @param [writes]
 * @param [elapsedTime]
 * @param [queryText]
 * @param [storedProc]
 * @param [command]
 * @param [loginName]
 * @param [hostName]
 * @param [programName]
 * @param [hostProcessId]
 * @param [lastRequestEndTime]
 * @param [loginTime]
 * @param [openTransactionCount]
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

data class BlockedQueriesInput(
    override val metricId: String = PerformanceEnums.BLOCKED_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String

) : MetricInput()

data class BlockedQueriesResult(val queryList: List<BlockedQueriesDTO>) : IMetricResult()