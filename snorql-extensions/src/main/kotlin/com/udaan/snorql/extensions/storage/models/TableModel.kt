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
 * Model class to hold table statistics for [TableMetric]
 *
 * @property name table name
 * @property rows number of rows
 * @property reserved reserved space for table
 * @property data space used by data in table
 * @property indexSize size of index
 * @property unused ununsed space in table
 * @constructor Create table statistics metric model
 */
data class TableDTO(
    val name: String,
    val rows: String,
    val reserved: String,
    val data: String,
    @ColumnName("index_size")
    val indexSize: String,
    val unused: String
)

/**
 * Model class to hold input for [TableMetric]
 *
 * @property metricId id of [TableMetric]
 * @property metricPeriod metric period
 * @property databaseName name of database
 * @property tableName name of table
 * @constructor Create Table Metric input model
 */
data class TableInput(
    override val metricId: String = StorageEnums.TABLE.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val tableName: String,
) : MetricInput()

/**
 * Model class to hold result of [TableMetric]
 *
 * @property queryList list of table statistics wrapped in [TableDTO]
 * @constructor Create table metric result model
 */
data class TableResult(val queryList: List<TableDTO>) : IMetricResult()
