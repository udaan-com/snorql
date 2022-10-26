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
 * Model class to hold column Schema for [TableSchemaMetric]
 *
 * @property columnName column name
 * @property createdDate creation date
 * @property isRowGuid is row guid flag
 * @property isIdentity is identity flag
 * @property ordinalPosition ordinal position
 * @property columnDefault deafult value for column
 * @property isNullable is nullable flag
 * @property dataType data type of column
 * @property characterMaximumLength maximum length of characters possible
 * @property collationName name of collation
 * @constructor Create column schema model
 */
data class TableSchemaDTO(
    val columnName: String,
    val createdDate: String,
    val isRowGuid: String,
    val isIdentity: String,
    val ordinalPosition: Int,
    val columnDefault: String?,
    val isNullable: String,
    val dataType: String,
    val characterMaximumLength: Int?,
    val collationName: String?
)

/**
 * Model class to hold input for [TableSchemaMetric]
 *
 * @property metricId id of [TableSchemaMetric]
 * @property metricPeriod period of metric
 * @property databaseName name of database
 * @property tableName name of table
 * @constructor Create Table schema metric input model
 */
data class TableSchemaInput(
    override val metricId: String = StorageEnums.TABLE_SCHEMA.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String,
    val tableName: String,
) : MetricInput()

/**
 * Model class to hold result of [TableSchemaMetric]
 *
 * @property queryList list of column schemas wrapped in [TableSchemaDTO]
 * @constructor Create Table schema metric result model
 */
data class TableSchemaResult(val queryList: List<TableSchemaDTO>) : IMetricResult()
