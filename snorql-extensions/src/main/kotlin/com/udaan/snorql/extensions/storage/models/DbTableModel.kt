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
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

/**
 * Data class for Data transfer object of index stats
 * @param [tableName]
 * @param [schemaName]
 * @param [rows]
 * @param [totalSpaceKB]
 * @param [totalSpaceMB]
 * @param [usedSpaceKB]
 * @param [usedSpaceMB]
 * @param [unusedSpaceKB]
 * @param [unusedSpaceMB]
 */
data class DbTableDTO(
    @ColumnName("TableName")
    val tableName: String,

    @ColumnName("SchemaName")
    val schemaName: String,

    val rows: String,

    @ColumnName("TotalSpaceKB")
    val totalSpaceKB: String,

    @ColumnName("TotalSpaceMB")
    val totalSpaceMB: String,

    @ColumnName("UsedSpaceKB")
    val usedSpaceKB: String,

    @ColumnName("UsedSpaceMB")
    val usedSpaceMB: String,

    @ColumnName("UnusedSpaceKB")
    val unusedSpaceKB: String,

    @ColumnName("UnusedSpaceMB")
    val unusedSpaceMB: String
)

data class DbTableInput(
    override val metricId: String = StorageEnums.DB_TABLES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
) : MetricInput()

data class DbTableResult(val queryList: List<DbTableDTO>) : IMetricResult()