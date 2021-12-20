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
 * Model class to hold persistent version store and transaction statistics for [PVSMetric]
 *
 * @property persistentVersionStoreSizeGb persistent version store size in GB
 * @property onlineIndexVersionStoreSizeGb online index version store size in GB
 * @property currentAbortedTransactionCount count of currently aborted transactions
 * @property abortedVersionCleanerStartTime aborted version cleaner start time
 * @property abortedVersionCleanerEndTime aborted version cleaner end time
 * @property oldestTransactionBeginTime oldest transaction begin time
 * @property activeTransactionSessionId active transaction session id
 * @property activeTransactionElapsedTimeSeconds active transaction elapsed time in seconds
 * @constructor Create [PVSMetric] model
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
 * Model class to hold input for Persistent Version Store Metric
 *
 * @property metricId id of Persistent Version Store metric
 * @property metricPeriod metric period
 * @property databaseName name of database
 * @constructor Create Persistent Version Store input model
 */
data class PVSInput(
    override val metricId: String = StorageEnums.PVS.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()

/**
 * Model class to hold result of persistent version store metric
 *
 * @property queryList list of PVS and transaction statistics wrapped in [PVSDTO]
 * @constructor Create Persistent Version Store result model
 */
data class PVSResult(val queryList: List<PVSDTO>) : IMetricResult()
