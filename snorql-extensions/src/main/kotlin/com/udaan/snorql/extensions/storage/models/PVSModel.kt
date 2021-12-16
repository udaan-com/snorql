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

package com.udaan.snorql.extensions.storage.models

import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

/**
 * P v s d t o
 *
 * @property persistentVersionStoreSizeGb
 * @property onlineIndexVersionStoreSizeGb
 * @property currentAbortedTransactionCount
 * @property abortedVersionCleanerStartTime
 * @property abortedVersionCleanerEndTime
 * @property oldestTransactionBeginTime
 * @property activeTransactionSessionId
 * @property activeTransactionElapsedTimeSeconds
 * @constructor Create empty P v s d t o
 */
data class PVSDTO(
    @ColumnName("persistent_version_store_size_gb")
    val persistentVersionStoreSizeGb: Int?,

    @ColumnName("online_index_version_store_size_gb")
    val onlineIndexVersionStoreSizeGb: Int?,

    @ColumnName("current_aborted_transaction_count")
    val currentAbortedTransactionCount: Int?,

    @ColumnName("aborted_version_cleaner_start_time")
    val abortedVersionCleanerStartTime: String?,

    @ColumnName("aborted_version_cleaner_end_time")
    val abortedVersionCleanerEndTime: String?,

    @ColumnName("oldest_transaction_begin_time")
    val oldestTransactionBeginTime: String?,

    @ColumnName("active_transaction_session_id")
    val activeTransactionSessionId: Int?,

    @ColumnName("active_transaction_elapsed_time_seconds")
    val activeTransactionElapsedTimeSeconds: Int?
)

/**
 * P v s input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @constructor Create empty P v s input
 */
data class PVSInput(
    override val metricId: String = StorageEnums.PVS.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()

/**
 * P v s result
 *
 * @property queryList
 * @constructor Create empty P v s result
 */
data class PVSResult(val queryList: List<PVSDTO>) : IMetricResult()
