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

package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.model.ActiveQueriesFilterAlertInput
import com.udaan.snorql.extensions.alerts.model.ActiveQueriesFilterAlertResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.ActiveQueryInput
import com.udaan.snorql.extensions.performance.models.ActiveQueryResult
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.MetricPeriod
import com.udaan.snorql.framework.models.MetricResponse

class ActiveQueriesAlert :
    IAlert<ActiveQueriesFilterAlertInput, ActiveQueriesFilterAlertResult, IAlertRecommendation> {

    override fun getAlertOutput(
        alertInput: ActiveQueriesFilterAlertInput, alertConfig: AlertConfigOutline
    ): AlertOutput<ActiveQueriesFilterAlertResult, IAlertRecommendation> {
        val metricResponse = SqlMetricManager.getMetric<ActiveQueryInput, ActiveQueryResult, IMetricRecommendation>(
            metricId = PerformanceEnums.ACTIVE_QUERIES.getId(),
            metricInput = ActiveQueryInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = alertInput.databaseName
            )
        )
        val queryList =
            (metricResponse as MetricResponse<ActiveQueryResult, IMetricRecommendation>).metricOutput.result.queryList
        var filteredQueryList = queryList
        if (alertInput.elapsedTimeThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                if (it.elapsedTime.toIntOrNull() != null) it.elapsedTime.toInt() >= alertInput.elapsedTimeThreshold
                else false
            }
        }
        if (alertInput.cpuTimeThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                if (it.cpuTime != null) it.cpuTime >= alertInput.cpuTimeThreshold
                else false
            }
        }
        if (alertInput.logicalReadsThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                if (it.logicalReads != null) it.logicalReads >= alertInput.logicalReadsThreshold
                else false
            }
        }
        if (alertInput.readsThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                if (it.reads != null) it.reads >= alertInput.readsThreshold
                else false
            }
        }
        if (alertInput.writesThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                if (it.writes != null) it.writes >= alertInput.writesThreshold
                else false
            }
        }
        if (alertInput.openTransactionCountThreshold != null) {
            filteredQueryList = filteredQueryList.filter {
                it.openTransactionCount >= alertInput.openTransactionCountThreshold
            }
        }
        val alertResult = ActiveQueriesFilterAlertResult(
            filteredMetricResult = ActiveQueryResult(
                queryList = filteredQueryList
            )
        )

        val isAlert = alertResult.filteredMetricResult.queryList.isNotEmpty()

        return AlertOutput(
            isAlert = isAlert,
            alertResult = alertResult,
            recommendation = null
        )
    }
}