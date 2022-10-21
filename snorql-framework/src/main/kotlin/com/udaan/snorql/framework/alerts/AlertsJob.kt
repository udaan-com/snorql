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

import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.AlertSeverity
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IAlertResult
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.Job
import org.quartz.JobExecutionContext

class AlertsJob<in T : AlertInput, O : IAlertResult, V : IAlertRecommendation> : Job {
    private val logger = SqlMetricManager.logger

    override fun execute(context: JobExecutionContext) {
        try {
            val mergedDataMap = context.mergedJobDataMap
            val alertInput: T = SnorqlConstants.objectMapper.readValue(
                mergedDataMap["alertInput"] as String,
                Class.forName(mergedDataMap["alertInputClass"] as String)
            ) as T
            val alertType = mergedDataMap["alertType"] as String
            logger.info("[AlertsJob] Alert Input: {}", alertInput)
            val alertConfig = getAlertConfigOutline(context)

            val alertResponse = AlertsManager.getAlertResponse<T, O, V>(alertType, alertInput, alertConfig)
            logger.info("[AlertsJob] Alert Response: {}", alertResponse)

            if (alertResponse.alertOutput.isAlert) {
                SqlMetricManager.queryExecutor.handleAlert(
                    alertConfig = alertConfig,
                    alertInput = alertInput,
                    alertOutput = alertResponse.alertOutput
                )
            } else {
                logger.info("[AlertsJob] Not an alert \n{} \n{}", alertInput, alertResponse)
            }
        } catch (e: Exception) {
            logger.error("[AlertsJob] There has been an error ${e.message}", e)
        }
    }

    private fun getAlertConfigOutline(context: JobExecutionContext): AlertConfigOutline {
        return AlertConfigOutline(
            alertType = context.mergedJobDataMap["alertType"] as String,
            alertNameString = context.mergedJobDataMap["alertNameString"] as String,
            description = context.trigger.description,
            severity = context.mergedJobDataMap["severity"] as AlertSeverity,
            configuredByName = context.mergedJobDataMap["configuredByName"] as String,
            configuredByEmail = context.mergedJobDataMap["configuredByEmail"] as String,
            watchIntervalInSeconds = context.mergedJobDataMap["watchIntervalInSeconds"] as Int,
            endAt = null
        )
    }
}