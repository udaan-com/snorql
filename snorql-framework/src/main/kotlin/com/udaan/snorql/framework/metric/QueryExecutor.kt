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

import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.HistoricalDataPurgeConfig
import com.udaan.snorql.framework.models.HistoricalDatabaseResult
import com.udaan.snorql.framework.models.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.models.SnorqlConstants
import java.sql.Statement

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
        databaseName: String,
        query: String,
        params: Map<String, *> = mapOf<String, Any>()
    ): List<T> {
        return connection.run(databaseName, query, T::class.java, params)
    }

    /**
     * Execute the query using the connection instance as [Statement]
     *
     * @param T
     * @param databaseName
     * @param query (raw query, should not be parameterised)
     * @param preHooks hooks to run with statement before running the query
     * @param postHooks hooks to run with statement after running the query
     * @return
     */
    fun <T> execute(
        databaseName: String,
        query: String,
        mapClass: Class<T>,
        preHooks: (Statement.() -> Unit)? = null,
        postHooks: (Statement.() -> Unit)? = null
    ): List<T> {
        return connection.run(databaseName, query, mapClass, preHooks, postHooks)
    }

    /**
     * Function used to read historical data stored.
     * Calls `getHistoricalData` function defined by the user to fetch the data
     * Note: [fetchHistoricalData] can work on pagination using [paginationParams]
     *
     * @param metricId read data belonging to a particular metricID
     * @param databaseName read data belonging to a particular database
     * @param params additional parameters passed to filter the data
     */
    fun fetchHistoricalData(
        metricId: String,
        databaseName: String,
        paginationParams: Map<String, *> = emptyMap<String, String>(),
        params: Map<String, *> = emptyMap<String, String>()
    ): HistoricalDatabaseResult {
        val storageBucketId: String = SnorqlConstants.HISTORICAL_DATA_BUCKET_ID
        return connection.getHistoricalData(storageBucketId, metricId, databaseName, paginationParams, params)
    }

    /**
     * Persist historical data to enable persistence in snorql
     * This function calls the user defined `connection.storeData` function
     *
     * @param storageId Unique identifier of the storage bucket where historical data is stored
     * @param historicalDataList Data to be stored as list of rows
     */
    fun persistHistoricalData(storageId: String, historicalDataList: List<HistoricalDatabaseSchemaDTO>) {
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
            rows.add(row)
        }
        connection.storeData(storageId, columns, rows.toList())
    }

    /**
     * Handle an alert
     */
    fun handleAlert(alertConfig: AlertConfigOutline, alertInput: AlertInput, alertOutput: AlertOutput<*, *>) {
        connection.handleAlert(alertConfig, alertInput, alertOutput)
    }

    /**
     * Purge historical data from user's database
     * Wrapper for [Connection.purgePersistedData] function
     *
     * @param storageId Unique identifier of the storage bucket where historical data is stored
     * @param purgingInfo List of data filters for purging the data
     */
    fun purgeHistoricalData(storageId: String, purgingInfo: List<HistoricalDataPurgeConfig>) {
        return connection.purgePersistedData(storageId, purgingInfo)
    }
}
