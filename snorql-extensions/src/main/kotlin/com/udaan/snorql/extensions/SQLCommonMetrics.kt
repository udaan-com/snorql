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

package com.udaan.snorql.extensions

import com.udaan.snorql.extensions.accesscontrol.AccessControlEnums
import com.udaan.snorql.extensions.accesscontrol.metrics.UserRoleMetric
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.metrics.*
import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.extensions.storage.metrics.*
import com.udaan.snorql.framework.metric.SqlMetricManager

object SQLCommonMetrics {
    fun initialize() {

        // register performance related metric here
        SqlMetricManager.addMetric(PerformanceEnums.BLOCKED_QUERIES.getId(), BlockedQueriesMetric())
        SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_QUERIES.getId(), ActiveQueriesMetric())
        SqlMetricManager.addMetric(PerformanceEnums.LONG_RUNNING_QUERIES.getId(), LongRunningQueriesMetric())
        SqlMetricManager.addMetric(PerformanceEnums.INDEX_STATS.getId(), IndexStatsMetric())
        SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_DDL.getId(), ActiveDDLMetric())

        // register access-control related metric here
        SqlMetricManager.addMetric(AccessControlEnums.USER_ROLE.getId(), UserRoleMetric())

        // register storage related metric here
        SqlMetricManager.addMetric(StorageEnums.DB.getId(), DbMetric())
        SqlMetricManager.addMetric(StorageEnums.DB_TABLES.getId(), DbTableMetric())
        SqlMetricManager.addMetric(StorageEnums.DB_INDEX.getId(), DbIndexMetric())
        SqlMetricManager.addMetric(StorageEnums.DB_GROWTH.getId(), DbGrowthMetric())
        SqlMetricManager.addMetric(StorageEnums.TABLE.getId(), TableMetric())
        SqlMetricManager.addMetric(StorageEnums.TABLE_UNUSED_INDEX.getId(), TableUnusedIndexMetric())
        SqlMetricManager.addMetric(StorageEnums.PVS.getId(), PVSMetric())
        SqlMetricManager.addMetric(StorageEnums.TABLE_SCHEMA.getId(), TableSchemaMetric())
    }
}
