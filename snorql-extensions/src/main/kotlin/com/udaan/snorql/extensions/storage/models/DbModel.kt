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
 * Model class to hold Database statistics for Database Metric
 *
 * @property databaseName name of the database
 * @property databaseSize size of the database
 * @property unallocatedSpace unallocated space in database
 * @property reserved reserved space in database
 * @property data used space by data in database
 * @property indexSize size of index in database
 * @property unused unused space in database
 * @constructor Create Database Statistics model
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
 * Model class to hold storage statistics for database metric
 *
 * @property dbTotalSize total size of database
 * @property databaseName name of database
 * @property databaseSize size of database
 * @property unallocatedSpace unallocated space in database
 * @property reserved reserved space in database
 * @property data space taken by data in database
 * @property indexSize size of index in database
 * @property unused unused space in database
 * @constructor Create Database Storage Size Statistics model
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
 * Model class to hold input for Database Metric
 *
 * @property metricId id of database metric
 * @property metricPeriod metric period
 * @property databaseName name of database
 * @property dbName name of database
 * @constructor Create Database Input Statistics model
 */
data class DbInput(
    override val metricId: String = StorageEnums.DB.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val dbName: String
) : MetricInput()

/**
 * Model class to hold result for database metric
 *
 * @property queryList list of database storage statistics wrapped in [DbStorageSize]
 * @constructor Create Database result model
 */
data class DbResult(val queryList: List<DbStorageSize>) : IMetricResult()