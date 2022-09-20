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
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName


/**
 * Wrapper class to hold individual attributes for ReplicationStateDTO lag Metric
 *
 * @property lastCommitTime
 * @constructor Create read replication lag wrapper
 */

data class ReplicationStateDTO(
    @ColumnName("last_redone_time")
    val lastRedoneTime: String,

    @ColumnName("last_received_time")
    val lastReceivedTime: String

)

/**
 * Wrapper class to hold individual attributes for Read Replication lag Metric
 *
 * @property replicationLagInMillis
 * @constructor Create read replication lag wrapper
 */

data class ReadReplicationLagDTO(
    val lastRedoneTimeInMillis: Long,
    val lastReceivedTimeInMillis: Long,
    val replicationLagInMillis: Long
)

/**
 * Wrapper class to hold Replication lag Metric input
 *
 * @property metricId id of ReplicationLag
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create ReadReplicationLagInput metric input
 */
data class ReadReplicationLagInput(
    override val metricId: String = PerformanceEnums.READ_REPLICATION_LAG.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val replicaDatabaseName: String
) : MetricInput()

/**
 * Wrapper class to hold ReadReplicationLag metric result
 *
 * <p>Result of query store is a list of metrics
 * which are wrapped using [ReadReplicationLagDTO]</p>
 *
 * @property queryList list of metrics wrapped in [ReadReplicationLagDTO]
 * @constructor Create empty ReadReplicationLag result
 */
data class ReadReplicationLagResult(val queryList: List<ReadReplicationLagDTO>) : IMetricResult()

/**
 * Model class to hold recommendations for [ReadReplicationMetric]
 *
 * @property text recommendation text
 * @constructor Create ReadReplication metric recommendation model
 */
data class ReadReplicationRecommendation(val text: String, val isActionRequired: Boolean) : IMetricRecommendation()