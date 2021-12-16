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

package com.udaan.snorql.extensions.storage.models

import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

/**
 * Table unused index d t o
 *
 * @property objectName
 * @property indexName
 * @property userSeeks
 * @property userScans
 * @property userLookups
 * @property userUpdates
 * @property columnName
 * @constructor Create empty Table unused index d t o
 */
data class TableUnusedIndexDTO(
    @ColumnName("OBJECT_NAME")
    val objectName: String,

    @ColumnName("INDEX_NAME")
    val indexName: String,

    @ColumnName("USER_SEEKS")
    val userSeeks: Int,

    @ColumnName("USER_SCANS")
    val userScans: Int,

    @ColumnName("USER_LOOKUPS")
    val userLookups: Int,

    @ColumnName("USER_UPDATES")
    val userUpdates: Int,
    val columnName: String
)

/**
 * Table unused index input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @property tableName
 * @constructor Create empty Table unused index input
 */
data class TableUnusedIndexInput(
    override val metricId: String = StorageEnums.TABLE_UNUSED_INDEX.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val tableName: String
) : MetricInput()

/**
 * Table unused index result
 *
 * @property queryList
 * @constructor Create empty Table unused index result
 */
data class TableUnusedIndexResult(val queryList: List<TableUnusedIndexDTO>) : IMetricResult()

/**
 * Table unused index recommendation
 *
 * @property indexesToDrop
 * @constructor Create empty Table unused index recommendation
 */
data class TableUnusedIndexRecommendation(val indexesToDrop: List<String>) : IMetricRecommendation()
