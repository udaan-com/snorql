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

import com.udaan.snorql.framework.SQLMonitoringException
import com.udaan.snorql.framework.TriggerNotFoundException
import com.udaan.snorql.framework.job.QuartzUtils
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.AlertResponse
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IAlertResult
import com.udaan.snorql.framework.models.SnorqlConstants
import com.udaan.snorql.framework.models.SnorqlConstants.objectMapper
import com.udaan.snorql.framework.models.TriggerBuildConfig
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobKey
import org.quartz.SimpleTrigger
import org.quartz.Trigger
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher

object AlertsManager {

    private val logger = SqlMetricManager.logger
    private val alertIdToAlertMap: MutableMap<String, Any> = mutableMapOf()

    fun addAlertToMap(
        alertId: String, instance: IAlert<*, *, *>
    ) {
        alertIdToAlertMap[alertId] = instance
    }

    fun <T : AlertInput, O : IAlertResult, V : IAlertRecommendation> addAlert(
        alertConfig: AlertConfigOutline,
        alertInput: T
    ): Boolean {
        val jobName = alertConfig.alertType
        val triggerName = "${alertInput.databaseName}-${alertConfig.alertType}".plus("-").plus(
            objectMapper.writeValueAsString(alertInput).hashCode()
        )
        val jobDataMap = JobDataMap()
        jobDataMap["alertInput"] = objectMapper.writeValueAsString(alertInput)
        jobDataMap["alertClass"] = alertIdToAlertMap[alertConfig.alertType]!!::class.java.name
        jobDataMap["alertInputClass"] = alertInput::class.java.name
        jobDataMap["alertType"] = alertConfig.alertType
        jobDataMap["severity"] = alertConfig.severity
        jobDataMap["alertNameString"] = alertConfig.alertNameString
        jobDataMap["configuredByName"] = alertConfig.configuredByName
        jobDataMap["configuredByEmail"] = alertConfig.configuredByEmail
        jobDataMap["watchIntervalInSeconds"] = alertConfig.watchIntervalInSeconds
        val jobKey = JobKey(jobName, SnorqlConstants.ALERT_GROUP_NAME)
        val triggerKey = TriggerKey(triggerName, SnorqlConstants.ALERT_GROUP_NAME)
        return if (!QuartzUtils.scheduler.checkExists(jobKey)) {
            logger.info("[AlertsManager][addAlert] Configuring a job with job key $jobKey")
            val triggerConfig = TriggerBuildConfig(
                triggerName = triggerName,
                description = alertConfig.description,
                job = null,
                jobDataMap = jobDataMap,
                intervalInSeconds = alertConfig.watchIntervalInSeconds,
                endAt = alertConfig.endAt
            )
            val job = JobBuilder.newJob(AlertsJob<T, O, V>().javaClass)
                .withIdentity(jobName, SnorqlConstants.ALERT_GROUP_NAME).storeDurably().build()
            val trigger: SimpleTrigger =
                QuartzUtils.buildSimpleTriggerObject(triggerConfig, SnorqlConstants.ALERT_GROUP_NAME)
            QuartzUtils.triggerJob(job, trigger)
        } else {
            val job = QuartzUtils.scheduler.getJobDetail(jobKey)
            val triggerConfig = TriggerBuildConfig(
                triggerName = triggerName,
                description = alertConfig.description,
                job = job,
                jobDataMap = jobDataMap,
                intervalInSeconds = alertConfig.watchIntervalInSeconds,
                endAt = alertConfig.endAt
            )
            if (!QuartzUtils.scheduler.checkExists(triggerKey)) {
                val trigger: SimpleTrigger =
                    QuartzUtils.buildSimpleTriggerObject(triggerConfig, SnorqlConstants.ALERT_GROUP_NAME)
                QuartzUtils.triggerJob(job = null, trigger = trigger)
            } else {
                val newTrigger: SimpleTrigger =
                    QuartzUtils.buildSimpleTriggerObject(triggerConfig, SnorqlConstants.ALERT_GROUP_NAME)
                QuartzUtils.replaceTrigger(triggerKey, newTrigger)
            }
        }
    }

    fun getAlerts(databaseName: String): List<Map<String, Any?>> {
        return getAllAlerts(databaseName)
    }

    fun getAlert(alertId: String, databaseName: String): Map<String, Any?> {
        val alertList = getAllAlerts(databaseName, alertId)
        if (alertList.isNotEmpty()) {
            return alertList[0]
        } else {
            throw TriggerNotFoundException("Alert with Alert ID $alertId not found.")
        }
    }

    fun <T : AlertInput, O : IAlertResult, V : IAlertRecommendation> getAlertResponse(
        alertId: String,
        alertInput: T,
        alertConfig: AlertConfigOutline
    ): AlertResponse<*, *> {
        val instance = alertIdToAlertMap[alertId]?.let { it as IAlert<T, O, V> }
            ?: throw SQLMonitoringException("IMetric impl instance not found for metric id [$alertId]")
        return instance.getAlertResponse(alertInput = alertInput, alertConfig = alertConfig)
    }

    fun deleteAlert(alertId: String): Boolean {
        return QuartzUtils.deleteTrigger(triggerName = alertId, triggerGroup = SnorqlConstants.ALERT_GROUP_NAME)
    }

    /**
     * Fetches triggers added to the scheduler based on metric id and database name
     *
     * @param databaseName name of database to fetch triggers for required database
     */
    private fun getAllAlerts(
        databaseName: String,
        alertId: String? = null,
        triggerGroup: String = SnorqlConstants.ALERT_GROUP_NAME
    ): List<Map<String, Any?>> {
        logger.info("[AlertsManager][getAllAlerts] Group is $triggerGroup")
        val allTriggerKeys = QuartzUtils.scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(triggerGroup))
        logger.info("[AlertsManager][getAllAlerts] Trigger Keys: $allTriggerKeys")
        val triggersList = mutableListOf<Trigger>()
        var triggerDetailsList: MutableList<Map<String, Any?>> = mutableListOf<Map<String, Any?>>()
        allTriggerKeys.forEach { triggerKey ->
            run {
                val trigger = QuartzUtils.scheduler.getTrigger(triggerKey)
                triggersList.add(trigger)
                val triggerDetailsMap = mutableMapOf<String, Any?>(
                    "triggerGroup" to trigger.key.group,
                    "alertName" to trigger.jobDataMap["alertNameString"],
                    "alertId" to trigger.key.name,
                    "alertType" to trigger.jobDataMap["alertType"],
                    "nextFireTime" to trigger.nextFireTime,
                    "endTime" to trigger.endTime,
                    "startTime" to trigger.startTime,
                    "description" to trigger.description,
                    "watchIntervalInSeconds" to trigger.jobDataMap["watchIntervalInSeconds"],
                    "configuredByName" to trigger.jobDataMap["configuredByName"],
                    "configuredByEmail" to trigger.jobDataMap["configuredByEmail"],
                    "severity" to trigger.jobDataMap["severity"]
                )
                val alertInputMap: Map<String, Any?> = objectMapper.readValue(
                    trigger.jobDataMap["alertInput"] as String, Map::class.java
                ) as Map<String, Any?>
                triggerDetailsMap.putAll(alertInputMap)
                triggerDetailsList.add(triggerDetailsMap.toMap())
            }
        }
        triggerDetailsList = triggerDetailsList.filter { triggerDetail ->
            triggerDetail["databaseName"] == databaseName
        } as MutableList<Map<String, Any?>>
        if (alertId != null) {
            triggerDetailsList = triggerDetailsList.filter { triggerDetail ->
                triggerDetail["alertId"] == alertId
            } as MutableList<Map<String, Any?>>
        }
        return triggerDetailsList
    }
}
