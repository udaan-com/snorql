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

package com.udaan.snorql.extensions.performance

import com.udaan.snorql.framework.IMtericId

/**
 * Performance metric enums
 *
 * @property metricId id of performance metric in context
 * @constructor Create Performance enums
 */
enum class PerformanceEnums(private val metricId:String):IMtericId {

    /**
     * Active Queries Metric ID mapping
     */
    ACTIVE_QUERIES("activeQueries"),

    /**
     * Long Running Queries Metric ID mapping
     */
    LONG_RUNNING_QUERIES("longRunningQueries"),

    /**
     * Blocked Queries Metric ID mapping
     */
    BLOCKED_QUERIES("blockedQueries"),

    /**
     * Index Fragmentation Metric ID mapping
     */
    INDEX_FRAGMENTATION("indexFragmentation"),

    /**
     * Index Stats Metric ID mapping
     */
    INDEX_STATS("indexStats"),

    /**
     * Compute Utilization Metric ID mapping
     */
    COMPUTE_UTILIZATION("computeUtilization"),

    /**
     * Query Plan Stats Metric ID mapping
     */
    QUERY_PLAN_STATS("queryPlanStats"),

    /**
     * Query Plan XML Metric ID mapping
     */
    QUERY_PLAN_XML("queryPlanXML"),

    /**
     * Query Store Metric ID mapping
     */
    QUERY_STORE("queryStore"),

    /**
     * Read Replication Lag Metric ID mapping
     */
    READ_REPLICATION_LAG("readReplicationLag"),

    /**
     * Geo Replica Lag Metric ID mapping
     */
    GEO_REPLICA_LAG("geoReplicaLag"),

    /**
     * Active DDL Queries Metric ID mapping
     */
    ACTIVE_DDL("activeDDL");

    override fun getId(): String {
        return "performance_" + this.metricId
    }
}