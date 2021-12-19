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
 * [Connection] interface defines methods to be overridden by the user for successful connection with user's database
 *
 * Methods defined in [Connection] are dependent on the user's database. User needs to override the methods for snorql
 * to successfully interact with the user's database.
 */
interface Connection {
    /**
     * Run binds to `<T>` by executing a query using a databaseName instance
     *
     * @param T
     * @param databaseName database name to create instance
     * @param query execute the query
     * @param mapClass
     * @param params parameters for metric
     * @return
     */
    fun <T> run(databaseName:String, query: String,
                mapClass: Class<T>,
                params: Map<String, *> = emptyMap<String, String>()): List<T>

    /**
     * [storeData] is used to write data into user's database
     *
     * @param databaseName name of database to write into
     * @param tableName name of table to write into
     * @param columns the columns that will be written
     * @param rows actual data rows to be written
     */
    fun storeData(databaseName:String, tableName: String,
                  columns: List<String>,
                  rows: List<List<Any>>)
}