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

package com.udaan.snorql.framework.metric

/**
 * Query executor
 *
 * @property connection
 * @constructor Create empty Query executor
 */
class QueryExecutor(val connection: Connection) {
    /**
     * Execute the query using the connection instance
     *
     * @param T
     * @param databaseName
     * @param query
     * @param params
     * @return
     */
    inline fun <reified T> execute(databaseName:String, query: String,
                                   params: Map<String, *> = mapOf<String, Any>()): List<T> {
        return connection.run(databaseName, query, T::class.java, params)
    }

    /**
     * Persist data to enable historical data in snorql
     *
     * @param databaseName
     * @param tableName
     * @param columns
     * @param rows
     */
    fun persistData(databaseName:String, tableName: String,
                    columns: List<String>,
                    rows: List<List<Any>>) {
        connection.storeData(databaseName,tableName, columns, rows)
    }

}