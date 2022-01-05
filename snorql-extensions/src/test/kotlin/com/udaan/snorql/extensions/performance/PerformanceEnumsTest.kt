/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.extensions.performance

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PerformanceEnumsTest {

    @Test
    fun testGetId() {
        assertNotNull(PerformanceEnums.ACTIVE_DDL)
        assertNotNull(PerformanceEnums.ACTIVE_QUERIES)
        assertNotNull(PerformanceEnums.BLOCKED_QUERIES)
        assertNotNull(PerformanceEnums.INDEX_STATS)
        assertNotNull(PerformanceEnums.LONG_RUNNING_QUERIES)

        assertEquals("performance_activeDDL", PerformanceEnums.ACTIVE_DDL.getId())
        assertEquals("performance_activeQueries", PerformanceEnums.ACTIVE_QUERIES.getId())
        assertEquals("performance_blockedQueries", PerformanceEnums.BLOCKED_QUERIES.getId())
        assertEquals("performance_indexStats", PerformanceEnums.INDEX_STATS.getId())
        assertEquals("performance_longRunningQueries", PerformanceEnums.LONG_RUNNING_QUERIES.getId())
    }
}