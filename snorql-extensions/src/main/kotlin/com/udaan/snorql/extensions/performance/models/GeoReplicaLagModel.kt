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
 * Wrapper class to hold individual attributes for Read Replication lag Metric
 *
 * @property partnerServer
 * @property partnerDatabase
 * @property replicationState
 * @property replicationStateDesc
 * @property roleDesc
 * @property secondaryAllowConnectionsDesc
 * @property lastReplication
 * @property replicationLagSec
 * @constructor Create geo replication lag wrapper
 */

data class GeoReplicaLagDTO(
    @ColumnName("partner_server")
    val partnerServer: String,
    @ColumnName("partner_database")
    val partnerDatabase: String,
    @ColumnName("replication_state")
    val replicationState: Int,
    @ColumnName("replication_state_desc")
    val replicationStateDesc: String,
    @ColumnName("role_desc")
    val roleDesc: String,
    @ColumnName("secondary_allow_connections_desc")
    val secondaryAllowConnectionsDesc: String,
    @ColumnName("last_replication")
    val lastReplication: String,
    @ColumnName("replication_lag_sec")
    val replicationLagSec: Long
)

/**
 * Wrapper class to hold Replication lag Metric input
 *
 * @property metricId id of ReplicationLag
 * @property metricPeriod
 * @property databaseName database on which metric is used
 * @constructor Create GeoReplicaLagInput metric input
 */
data class GeoReplicaLagInput(
    override val metricId: String = PerformanceEnums.GEO_REPLICA_LAG.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val primaryDatabaseName: String
) : MetricInput()

/**
 * Wrapper class to hold GeoReplicaLag metric result
 *
 * Result of query store is a list of metrics
 * which are wrapped using [GeoReplicaLagDTO]
 *
 * @property queryList list of metrics wrapped in [GeoReplicaLagDTO]
 * @constructor Create empty GeoReplicaLag result
 */
data class GeoReplicaLagResult(val queryList: List<GeoReplicaLagDTO>) : IMetricResult()

/**
 * Model class to hold recommendations for GeoReplicaMetric
 *
 * @property text recommendation text
 * @constructor Create GeoReplica metric recommendation model
 */
data class GeoReplicaLagRecommendation(val text: String, val isActionRequired: Boolean) : IMetricRecommendation()
