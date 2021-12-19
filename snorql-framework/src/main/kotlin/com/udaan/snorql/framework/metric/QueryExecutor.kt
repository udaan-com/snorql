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
 * Query Executor class responsible for interacting with [Connection] interface child class
 *
 * @property connection instance of user's database connection
 * @constructor Create Query executor instance
 */
class QueryExecutor(val connection: Connection) {
    /**
     * Execute the query using the connection instance
     *
     * @param T [query] result row's model class
     * @param databaseName name of database to query
     * @param query actual query string
     * @param params parameters to be sent along with the [query]
     * @return Query result. Returned as list of individual rows`
     */
    inline fun <reified T> execute(databaseName:String, query: String,
                                   params: Map<String, *> = mapOf<String, Any>()): List<T> {
        return connection.run(databaseName, query, T::class.java, params)
    }

    /**
     * Persist data in database
     *
     * @param databaseName name of database to store data in
     * @param tableName name to table to store data in
     * @param columns columns to be stored
     * @param rows actual data to be stored
     */
    fun persistData(databaseName:String, tableName: String,
                    columns: List<String>,
                    rows: List<List<Any>>) {
        connection.storeData(databaseName,tableName, columns, rows)
    }

}