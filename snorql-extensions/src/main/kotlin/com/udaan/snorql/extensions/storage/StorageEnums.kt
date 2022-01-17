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

package com.udaan.snorql.extensions.storage

import com.udaan.snorql.framework.IMtericId

/**
 * Storage metric ids enum class
 *
 * @property metricId id of metric in context
 * @constructor Create Storage metric enums
 */
enum class StorageEnums(private val metricId: String) : IMtericId {

    /**
     * Database
     */
    DB("db"),

    /**
     * Database Tables metric id
     */
    DB_TABLES("dbTables"),

    /**
     * Database Index metric id
     */
    DB_INDEX("dbIndex"),

    /**
     * Database Growth metric id
     */
    DB_GROWTH("dbGrowth"),

    /**
     * Table metric id
     */
    TABLE("table"),

    /**
     * Table Unused Index metric id
     */
    TABLE_UNUSED_INDEX("tableUnusedIndex"),

    /**
     * Persistent version store metric id
     */
    PVS("pvs"),

    /**
     * Table schema metric id
     */
    TABLE_SCHEMA("tableSchema");

    override fun getId(): String {
        return "storage_" + this.metricId
    }
}
