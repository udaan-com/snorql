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

/**
 * Data class for Data transfer object of db index
 * @param [rows]
 * @param [indexName]
 * @param [tableName]
 * @param [totalSpaceMB]
 * @param [usedSpaceMB]
 * @param [unusedSpaceMB]
 */
data class DbIndexDTO(
    val rows: String,
    val indexName: String?,
    val tableName: String,
    val totalSpaceMB: String,
    val usedSpaceMB: String,
    val unusedSpaceMB: String
)

data class DbIndexInput(
    override val metricId: String = StorageEnums.DB_INDEX.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
) : MetricInput()

data class DbIndexResult(val queryList: List<DbIndexDTO>) : IMetricResult()