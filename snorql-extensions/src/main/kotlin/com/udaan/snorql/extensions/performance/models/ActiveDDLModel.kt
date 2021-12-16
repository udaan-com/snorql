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
 * Active d d l d t o
 *
 * @property currentStep
 * @property queryText
 * @property totalRows
 * @property rowsProcessed
 * @property rowsLeft
 * @property percentComplete
 * @property elapsedSeconds
 * @property estimatedSecondsLeft
 * @property estimatedCompletionTime
 * @constructor Create empty Active d d l d t o
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
 * Active d d l input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @constructor Create empty Active d d l input
 */
data class ActiveDDLInput(
    override val metricId: String = PerformanceEnums.ACTIVE_DDL.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
) : MetricInput()

/**
 * Active d d l result
 *
 * @property queryList
 * @constructor Create empty Active d d l result
 */
data class ActiveDDLResult(val queryList: List<ActiveDDLDTO>) : IMetricResult()