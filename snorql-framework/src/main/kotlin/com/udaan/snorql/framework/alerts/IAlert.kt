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

package com.udaan.snorql.framework.alerts

import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.AlertResponse
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IAlertResult

interface IAlert<in T : AlertInput, O : IAlertResult, V : IAlertRecommendation> {

    fun getAlertRecommendation(alertResult: O): V? {
        return null
    }

    fun getAlertOutput(alertInput: T, alertConfig: AlertConfigOutline): AlertOutput<O, V>

    fun getAlertResponse(alertInput: T, alertConfig: AlertConfigOutline): AlertResponse<*, *> {
        val alertOutput = getAlertOutput(alertInput, alertConfig)
        return AlertResponse<O, V>(
            alertInput = alertInput,
            alertOutput = alertOutput
        )
    }
}