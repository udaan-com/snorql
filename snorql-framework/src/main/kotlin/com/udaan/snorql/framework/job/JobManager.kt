/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework.job

import com.fasterxml.jackson.databind.ObjectMapper
import com.udaan.snorql.framework.TriggerNotFoundException
import com.udaan.snorql.framework.job.model.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.job.model.RecordingJobConfigOutline
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.sql.Timestamp
import java.util.*

object JobManager {

    private var schedulerFactory: StdSchedulerFactory = StdSchedulerFactory()
    private var scheduler: Scheduler = schedulerFactory.scheduler

    fun initializeJobScheduler(quartzProperties: Properties?) {
        schedulerFactory = if (quartzProperties != null) StdSchedulerFactory(quartzProperties)
        else StdSchedulerFactory()
        scheduler = schedulerFactory.scheduler
        startScheduler()
    }

    private val objectMapper: ObjectMapper = SnorqlConstants.objectMapper

    private fun startScheduler() {
        try {
            scheduler.start()
        } catch (e: Exception) {
            print("Scheduler start failed due to $e")
        }
    }

    /**
     * [addJob] does the following:
     * 1. Configures a trigger with record data job and gets the trigger id
     * 2. If successful, Saves the following in the database: triggerId, metricId, databaseName, metricInput
     * 3. Returns true if successful
     */
    fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> addJob(
        jobConfig: RecordingJobConfigOutline,
        metricInput: T,
    ): Boolean {
        return try {
            val intervalInSeconds = jobConfig.watchIntervalInSeconds
            val startFrom = jobConfig.startFrom
            val endAt = jobConfig.endAt
            val description = jobConfig.description
            val configuredByName = jobConfig.configuredByName
            val configuredByEmail = jobConfig.configuredByEmail
            val configSuccess: Boolean =
                configureJobAndTrigger<T, O, V>(metricInput, intervalInSeconds, startFrom, endAt, description, configuredByName, configuredByEmail)
            configSuccess
        } catch (e: Exception) {
            print("Unable to add data recording: $e")
            false
        }
    }

    /**
     * Fetch historical data for a metricId and databaseName
     */
    fun getHistoricalData(
        metricId: String,
        databaseName: String,
        pageNumber: Int,
        pageSize: Int,
        params: Map<String, *> = mapOf<String, String>()
    ): List<HistoricalDatabaseSchemaDTO> {
        return SqlMetricManager.queryExecutor.fetchHistoricalData(metricId, databaseName, pageNumber, pageSize, params)
    }

    private fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> configureJobAndTrigger(
        metricInput: T,
        intervalInSeconds: Int,
        startFrom: Timestamp?,
        endAt: Timestamp?,
        description: String?,
        configuredByName: String?,
        configuredByEmail: String?
    ): Boolean {
        val jobName: String = metricInput.metricId // jobName = metricId (Therefore, for each metric, there is a job
        val triggerName: String =
            metricInput.metricId.plus("_").plus(metricInput.databaseName).plus("_")
                .plus(SnorqlConstants.objectMapper.writeValueAsString(metricInput).hashCode())
        val jobDataMap = JobDataMap()
        jobDataMap["metricInput"] =
            objectMapper.writeValueAsString(metricInput) // gson.toJson(metricInput).toString() // Use Jackson
        jobDataMap["inputClass"] = metricInput::class.java.name
        jobDataMap["configuredByName"] = configuredByName
        jobDataMap["configuredByEmail"] = configuredByEmail
        jobDataMap["repeatInterval"] = intervalInSeconds
        val jobKey = JobKey(jobName, SnorqlConstants.MONITORING_GROUP_NAME)
        val triggerKey = TriggerKey(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
        return if (!scheduler.checkExists(jobKey)) {
            println("Job does not exist. Configuring a job with job key $jobKey")
            val job = JobBuilder.newJob(MonitoringJob<T, O, V>().javaClass)
                .withIdentity(jobName, SnorqlConstants.MONITORING_GROUP_NAME)
                .storeDurably()
                .build()
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
                    .withDescription(description)
//                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalInSeconds)
                            .repeatForever()
                    )
                    .endAt(endAt)
                    .build()
            triggerJob(job, trigger)
        } else {
            println("Job already exists with job key $jobKey")
            val job = scheduler.getJobDetail(jobKey)
            if (!scheduler.checkExists(triggerKey)) {
                val trigger: SimpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
                    .withDescription(description)
                    .forJob(job)
//                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalInSeconds)
                            .repeatForever()
                    )
                    .endAt(endAt)
                    .build()
                triggerJob(job = null, trigger = trigger)
            } else {
                val newTrigger: SimpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
                    .withDescription(description)
                    .forJob(job)
//                        .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalInSeconds)
                            .repeatForever()
                    )
                    .endAt(endAt)
                    .build()
                replaceTrigger(triggerKey, newTrigger)
            }
        }
    }

    fun getAllMonitoringTriggers(
        metricId: String,
        databaseName: String
    ): List<Map<String, Any?>> {
        val group: String = SnorqlConstants.MONITORING_GROUP_NAME
        val allTriggerKeys =
            scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group)) // groupEquals(SnorqlConstants.MONITORING_GROUP_NAME))
        val triggersList = mutableListOf<Trigger>()
        var triggerDetailsList: MutableList<Map<String, Any?>> = mutableListOf<Map<String, Any?>>()
        allTriggerKeys.forEach { triggerKey ->
            run {
                val trigger = scheduler.getTrigger(triggerKey)
                triggersList.add(trigger)
                val triggerDetailsMap = mutableMapOf<String, Any?>(
                    "triggerGroup" to trigger.key.group,
                    "triggerName" to trigger.key.name,
                    "nextFireTime" to trigger.nextFireTime,
                    "endTime" to trigger.endTime,
                    "startTime" to trigger.startTime,
                    "description" to trigger.description,
                    "repeatInterval" to trigger.jobDataMap["repeatInterval"],
                    "configuredByName" to trigger.jobDataMap["configuredByName"],
                    "configuredByEmail" to trigger.jobDataMap["configuredByEmail"]
                )
                val metricInputMap: Map<String, Any?> =
                    objectMapper.readValue(
                        trigger.jobDataMap["metricInput"] as String,
                        Map::class.java
                    ) as Map<String, Any?>
                triggerDetailsMap.putAll(metricInputMap)
                triggerDetailsList.add(triggerDetailsMap.toMap())
            }
        }
        triggerDetailsList = triggerDetailsList.filter { triggerDetail ->
            triggerDetail["metricId"] == metricId && triggerDetail["databaseName"] == databaseName
        } as MutableList<Map<String, Any?>>
        triggerDetailsList.forEach {
            println("Element: $it")
        }
        return triggerDetailsList
    }

    fun removeEverything() {
        scheduler.clear()
    }

    fun deleteTrigger(
        triggerName: String
    ): Boolean {
        return try {
            if (scheduler.checkExists(TriggerKey(triggerName, SnorqlConstants.MONITORING_GROUP_NAME))) {
                println("Trigger exists: ")
                scheduler.unscheduleJob(TriggerKey(triggerName, SnorqlConstants.MONITORING_GROUP_NAME))
                true
            } else {
                throw TriggerNotFoundException("Trigger with name $triggerName not found")
            }
        } catch (e: TriggerNotFoundException) {
            println("Trigger not found: $e")
            false
        } catch (e: Exception) {
            println("Failed to stop data recording: $e")
            false
        }
    }

    private fun replaceTrigger(triggerKey: TriggerKey, newTrigger: SimpleTrigger): Boolean {
        return try {
            scheduler.rescheduleJob(triggerKey, newTrigger)
            true
        } catch (e: Exception) {
            print("Trigger replacement failed due to $e")
            false
        }
    }

    private fun triggerJob(
        job: JobDetail?,
        trigger: SimpleTrigger
    ): Boolean {
        return try {
            if (job == null) scheduler.scheduleJob(trigger)
            else scheduler.scheduleJob(job, trigger)
            true
        } catch (e: Exception) {
            print("Job Scheduling failed due to $e")
            false
        }
    }
}