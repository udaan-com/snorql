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
 * Connection interface implemented by user to define methods required for snorql to interact with user's database
 */
interface Connection {
    /**
     * Run binds to <T> by executing a query using a databaseName instance
     *
     * @param T mapping model class
     * @param databaseName database name to create instance
     * @param query execute the query
     * @param mapClass mapping class
     * @param params parameters for metric
     * @return
     */
    fun <T> run(databaseName:String, query: String,
                mapClass: Class<T>,
                params: Map<String, *> = emptyMap<String, String>()): List<T>

    /**
     * Store data into user's database
     *
     * @param databaseName name of database to store data in
     * @param tableName name of table to store data in
     * @param columns list of column names
     * @param rows list of rows of data to be stored
     */
    fun storeData(databaseName:String, tableName: String,
                  columns: List<String>,
                  rows: List<List<Any>>)
}