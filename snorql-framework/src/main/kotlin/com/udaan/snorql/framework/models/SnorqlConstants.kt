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

package com.udaan.snorql.framework.models

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object SnorqlConstants {
    val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).registerKotlinModule()
        }

    const val DATA_PERSISTENCE_GROUP_NAME = "DATA_PERSISTENCE"
    const val ALERT_GROUP_NAME = "SNORQL_ALERTING"
    const val DATA_PURGE_GROUP_NAME = "DATA_PURGE"

    var HISTORICAL_DATA_BUCKET_ID = "sql_monitoring_historical_data"

    val historicalDataTableColumns =
        listOf("runId", "timestamp", "metricId", "databaseName", "source", "metricInput", "metricOutput")
}
