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
 * Performance enums
 *
 * @property metricId
 * @constructor Create empty Performance enums
 */
enum class PerformanceEnums(private val metricId:String):IMtericId {

    /**
     * A c t i v e_q u e r i e s
     *
     * @constructor Create empty A c t i v e_q u e r i e s
     */
    ACTIVE_QUERIES("activeQueries"),

    /**
     * L o n g_r u n n i n g_q u e r i e s
     *
     * @constructor Create empty L o n g_r u n n i n g_q u e r i e s
     */
    LONG_RUNNING_QUERIES("longRunningQueries"),

    /**
     * B l o c k e d_q u e r i e s
     *
     * @constructor Create empty B l o c k e d_q u e r i e s
     */
    BLOCKED_QUERIES("blockedQueries"),

    /**
     * I n d e x_s t a t s
     *
     * @constructor Create empty I n d e x_s t a t s
     */
    INDEX_STATS("indexStats"),

    /**
     * A c t i v e_d d l
     *
     * @constructor Create empty A c t i v e_d d l
     */
    ACTIVE_DDL("activeDDL");

    override fun getId(): String {
        return "performance_" + this.metricId
    }
}