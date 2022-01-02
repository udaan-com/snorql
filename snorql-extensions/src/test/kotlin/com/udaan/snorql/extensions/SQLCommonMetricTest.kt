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
import com.udaan.snorql.extensions.performance.metrics.ActiveDDLMetric
import com.udaan.snorql.extensions.performance.metrics.ActiveQueriesMetric
import com.udaan.snorql.extensions.performance.metrics.BlockedQueriesMetric
import com.udaan.snorql.extensions.performance.metrics.IndexStatsMetric
import com.udaan.snorql.extensions.performance.metrics.LongRunningQueriesMetric
import com.udaan.snorql.extensions.performance.models.BlockedQueriesInput
import com.udaan.snorql.extensions.performance.models.BlockedQueriesResult
import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.extensions.storage.metrics.DbGrowthMetric
import com.udaan.snorql.extensions.storage.metrics.DbIndexMetric
import com.udaan.snorql.extensions.storage.metrics.DbMetric
import com.udaan.snorql.extensions.storage.metrics.DbTableMetric
import com.udaan.snorql.extensions.storage.metrics.PVSMetric
import com.udaan.snorql.extensions.storage.metrics.TableMetric
import com.udaan.snorql.extensions.storage.metrics.TableSchemaMetric
import com.udaan.snorql.extensions.storage.metrics.TableUnusedIndexMetric
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import io.mockk.MockKObjectScope
import io.mockk.Ordering
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import kotlin.test.assertEquals

class SQLCommonMetricTest {
    @Before
    fun beforeTests() {
        mockkObject(SQLCommonMetrics)
        mockkObject(SqlMetricManager)
    }

    @Test
    fun willUseMockBehaviour() {
        SQLCommonMetrics.initialize()
        verify(exactly = 1) { SqlMetricManager.addMetric(PerformanceEnums.BLOCKED_QUERIES.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_DDL.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_QUERIES.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(PerformanceEnums.INDEX_STATS.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(PerformanceEnums.LONG_RUNNING_QUERIES.getId(), any()) }

        verify(exactly = 1) { SqlMetricManager.addMetric(AccessControlEnums.USER_ROLE.getId(), any()) }

        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.DB.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.DB_TABLES.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.DB_INDEX.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.DB_GROWTH.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.TABLE.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.TABLE_UNUSED_INDEX.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.TABLE_SCHEMA.getId(), any()) }
        verify(exactly = 1) { SqlMetricManager.addMetric(StorageEnums.PVS.getId(), any()) }

    }

    @After
    fun afterTests() {
        unmockkAll()
        // or unmockkObject(ObjBeingMocked)
    }
}