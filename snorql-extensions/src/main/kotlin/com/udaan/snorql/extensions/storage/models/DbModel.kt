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
 * Db d t o
 *
 * @property databaseName
 * @property databaseSize
 * @property unallocatedSpace
 * @property reserved
 * @property data
 * @property indexSize
 * @property unused
 * @constructor Create empty Db d t o
 */
data class DbDTO(
    @ColumnName("database_name")
    val databaseName: String,

    @ColumnName("database_size")
    val databaseSize: String,

    @ColumnName("unallocated space")
    val unallocatedSpace: String,

    val reserved: String,
    val data: String,

    @ColumnName("index_size")
    val indexSize: String,

    val unused: String
)

/**
 * Db storage size
 *
 * @property dbTotalSize
 * @property databaseName
 * @property databaseSize
 * @property unallocatedSpace
 * @property reserved
 * @property data
 * @property indexSize
 * @property unused
 * @constructor Create empty Db storage size
 */
data class DbStorageSize(
    val dbTotalSize: Int,

    @ColumnName("database_name")
    val databaseName: String,

    @ColumnName("database_size")
    val databaseSize: String,

    @ColumnName("unallocated space")
    val unallocatedSpace: String,

    val reserved: String,
    val data: String,

    @ColumnName("index_size")
    val indexSize: String,

    val unused: String
)

/**
 * Db input
 *
 * @property metricId
 * @property metricPeriod
 * @property databaseName
 * @property dbName
 * @constructor Create empty Db input
 */
data class DbInput(
    override val metricId: String = StorageEnums.DB.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val dbName:String
) : MetricInput()

/**
 * Db result
 *
 * @property queryList
 * @constructor Create empty Db result
 */
data class DbResult(val queryList: List<DbStorageSize>) : IMetricResult()