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
 * Storage enums
 *
 * @property metricId
 * @constructor Create empty Storage enums
 */
enum class StorageEnums(private val metricId:String): IMtericId {

    /**
     * D b
     *
     * @constructor Create empty D b
     */
    DB("db"),

    /**
     * D b_t a b l e s
     *
     * @constructor Create empty D b_t a b l e s
     */
    DB_TABLES("dbTables"),

    /**
     * D b_i n d e x
     *
     * @constructor Create empty D b_i n d e x
     */
    DB_INDEX("dbIndex"),

    /**
     * D b_g r o w t h
     *
     * @constructor Create empty D b_g r o w t h
     */
    DB_GROWTH("dbGrowth"),

    /**
     * T a b l e
     *
     * @constructor Create empty T a b l e
     */
    TABLE("table"),

    /**
     * T a b l e_u n u s e d_i n d e x
     *
     * @constructor Create empty T a b l e_u n u s e d_i n d e x
     */
    TABLE_UNUSED_INDEX("tableUnusedIndex"),

    /**
     * P v s
     *
     * @constructor Create empty P v s
     */
    PVS("pvs"),

    /**
     * T a b l e_s c h e m a
     *
     * @constructor Create empty T a b l e_s c h e m a
     */
    TABLE_SCHEMA("tableSchema");

    override fun getId(): String {
        return "storage_" + this.metricId
    }
}
