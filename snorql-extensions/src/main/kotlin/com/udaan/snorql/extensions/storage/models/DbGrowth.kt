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
 * @param [startTime]
 * @param [endTime]
 * @param [storageInMegabytes]
 */
data class DbGrowthDTO(
    @ColumnName("start_time")
    val startTime: String,

    @ColumnName("end_time")
    val endTime: String,

    @ColumnName("storage_in_megabytes")
    val storageInMegabytes: String
)

data class DbGrowthInput(
    override val metricId: String = StorageEnums.DB_GROWTH.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,

) : MetricInput()

data class DbGrowthResult(val queryList: List<DbGrowthDTO>) : IMetricResult()