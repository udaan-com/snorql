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

import java.sql.Timestamp

/**
 * Alert config model class
 */
data class AlertConfigOutline(
    val alertType: String,
    val alertNameString: String,
    val description: String?,
    val severity: AlertSeverity,
    val configuredByName: String?,
    val configuredByEmail: String?,
    val watchIntervalInSeconds: Int,
    val endAt: Timestamp?
)

/**
 * Abstract alert input class
 */
abstract class AlertInput {
    abstract val databaseName: String
}

/**
 * Alert Output data class
 */
data class AlertOutput<T : IAlertResult, V : IAlertRecommendation>(
    val isAlert: Boolean,
    val alertResult: T,
    val recommendation: V?
)

enum class AlertSeverity {
    CRITICAL, ERROR, WARNING, INFORMATIONAL, VERBOSE
}

/**
 * Response of an alert
 */
data class AlertResponse<T : IAlertResult, V : IAlertRecommendation>(
    val alertInput: AlertInput, val alertOutput: AlertOutput<*, *>
)

/**
 * Abstract alert result class
 */
abstract class IAlertResult

/**
 * Alert recommendation
 */
abstract class IAlertRecommendation
