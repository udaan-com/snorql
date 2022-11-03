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

package com.udaan.snorql.extensions.session.models

import com.udaan.snorql.extensions.session.SessionEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

data class SessionLocksInput(
    override val metricId: String = SessionEnums.SESSION_LOCKS.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val sessionId: Int
) : MetricInput()

data class SessionLocksDTO(
    @ColumnName("request_session_id")
    val sessionId: Int,
    @ColumnName("resource_database_id")
    val databaseId: Int,
    @ColumnName("request_lifetime")
    val requestLifetime: Long,
    @ColumnName("dbname")
    val dbName: String,
    @ColumnName("ObjectName")
    val objectName: String,
    @ColumnName("index_id")
    val indexId: Int?,
    @ColumnName("index_name")
    val indexName: String?,
    @ColumnName("resource_type")
    val resourceType: String,
    @ColumnName("resource_description")
    val resourceDescription: String?,
    @ColumnName("resource_associated_entity_id")
    val resourceAssociatedEntityId: String,
    @ColumnName("request_mode")
    val requestMode: String,
    @ColumnName("request_status")
    val requestStatus: String
)

data class SessionLocksResult(val queryList: List<SessionLocksDTO>) : IMetricResult()
