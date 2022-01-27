/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework.metric

import com.udaan.snorql.framework.models.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.models.SnorqlConstants

/**
 * Class to hold functions which interact with user defined query executor functions
 *
 * @property connection user's database connection
 * @constructor Create Query executor instance
 */
class QueryExecutor(val connection: Connection) {
    /**
     * Execute the query using the connection instance
     *
     * @param T mapping class model
     * @param databaseName name of database in context
     * @param query actual query string
     * @param params parameters
     * @return list of rows wrapped in mapping model class [T]
     */
    inline fun <reified T> execute(
        databaseName: String, query: String,
        params: Map<String, *> = mapOf<String, Any>()
    ): List<T> {
        return connection.run(databaseName, query, T::class.java, params)
    }

    /**
     * Persist data to enable historical data in snorql
     *
     * @param databaseName name of database to store data in
     * @param tableName name of table to store data in
     * @param columns columns to be written
     * @param rows actual data rows to be written
     */
    fun persistData(
        storageBucketId: String,
        columns: List<String>,
        rows: List<List<Any>>
    ) {
        connection.storeData(storageBucketId, columns, rows)
    }

    fun fetchHistoricalData(
        metricId: String,
        databaseName: String,
        pageNumber: Int,
        pageSize: Int,
        params: Map<String, *> = emptyMap<String, String>()
    ): List<HistoricalDatabaseSchemaDTO> {
        val storageBucketId: String = SnorqlConstants.HISTORICAL_DATA_BUCKET_ID
        return connection.getHistoricalData(storageBucketId, metricId, databaseName, pageNumber, pageSize, params)
    }

    fun persistHistoricalData(
        storageId: String,
        historicalDataList: List<HistoricalDatabaseSchemaDTO>,
    ) {
        val columns = SnorqlConstants.historicalDataTableColumns
        val rows = mutableListOf<List<String>>()
        historicalDataList.forEach {
            val row = mutableListOf<String>()
            row.add(it.runId)
            row.add(it.timestamp.toString())
            row.add(it.metricId)
            row.add(it.databaseName)
            row.add(it.source)
            row.add(it.metricInput)
            row.add(it.metricOutput)
            // row.add(SnorqlConstants.objectMapper.writeValueAsString(it.metricInput))
            // row.add(SnorqlConstants.objectMapper.writeValueAsString(it.metricOutput))
            rows.add(row)
        }
        connection.storeData(storageId, columns, rows.toList())
        println("Following data stored in historical database: $rows")
    }

    fun persistJobConfigData(metricId: String, databaseName: String, triggerName: String): Boolean {
        println(
            """Following data has been saved in the database: 
            |1. Metric ID: $metricId 
            |2. Database Name: $databaseName
            |3. Trigger Key: $triggerName""".trimMargin()
        )
        return true
    }

    fun removeFromDatabase(metricId: String, databaseName: String, triggerName: String): Boolean {
        println(
            """Following data has been removed from the database: 
            |1. Metric ID: $metricId 
            |2. Database Name: $databaseName
            |3. Trigger Key: $triggerName""".trimMargin()
        )
        return true
    }
}