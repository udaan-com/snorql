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
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.metric.logger
import com.udaan.snorql.framework.models.*
import org.quartz.Scheduler
import org.quartz.SimpleTrigger
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.JobDetail
import org.quartz.JobDataMap
import org.quartz.SimpleScheduleBuilder
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.sql.Timestamp
import java.util.Properties

object JobManager {

    private var schedulerFactory: StdSchedulerFactory = StdSchedulerFactory()
    private var scheduler: Scheduler = schedulerFactory.scheduler

    /**
     * Function to initialize Quartz Job Scheduler
     *
     * To be called by the user to initialize Job Scheduling in snorql
     *
     * @param quartzProperties Quartz properties as required by the user. Also defines the Quartz Job Store
     */
    fun initializeJobScheduler(quartzProperties: Properties?) {
        schedulerFactory = if (quartzProperties != null) StdSchedulerFactory(quartzProperties)
        else StdSchedulerFactory()
        scheduler = schedulerFactory.scheduler
        startScheduler()
    }

    private val objectMapper: ObjectMapper = SnorqlConstants.objectMapper

    val logger = SqlMetricManager.logger

    /**
     * Function to start a job scheduler
     */
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
        metricInput: T
    ): Boolean {
        val metricConfig: MetricConfig = SqlMetricManager.configuration.get(metricId = metricInput.metricId)
        if (!metricConfig.supportsHistorical) {
            throw UnsupportedOperationException("Historical Data is not supported for metric: ${metricInput.metricId}")
        }
        return try {
            val intervalInSeconds = jobConfig.watchIntervalInSeconds
            val endAt = jobConfig.endAt
            val description = jobConfig.description
            val configuredByName = jobConfig.configuredByName
            val configuredByEmail = jobConfig.configuredByEmail
            val configSuccess: Boolean =
                configureJobAndTrigger<T, O, V>(
                    metricInput,
                    intervalInSeconds,
                    endAt,
                    description,
                    configuredByName,
                    configuredByEmail
                )
            configSuccess
        } catch (e: Exception) {
            logger.error(
                "[addJob] Unable to add job and trigger for ${metricInput.metricId}: ${metricInput.databaseName}",
                e.stackTrace
            )
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
            objectMapper.writeValueAsString(metricInput)
        jobDataMap["inputClass"] = metricInput::class.java.name
        jobDataMap["configuredByName"] = configuredByName
        jobDataMap["configuredByEmail"] = configuredByEmail
        jobDataMap["repeatInterval"] = intervalInSeconds
        val jobKey = JobKey(jobName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME)
        val triggerKey = TriggerKey(triggerName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME)
        return if (!scheduler.checkExists(jobKey)) {
            logger.info("[configureJobAndTrigger] Configuring a job with job key $jobKey")
            val triggerConfig = TriggerBuildConfig(
                triggerName = triggerName, description = description, job = null,
                jobDataMap = jobDataMap, intervalInSeconds = intervalInSeconds, endAt = endAt
            )
            val job = JobBuilder.newJob(DataPersistenceJob<T, O, V>().javaClass)
                .withIdentity(jobName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME)
                .storeDurably()
                .build()
            val trigger: SimpleTrigger = buildTriggerObject(triggerConfig)
            triggerJob(job, trigger)
        } else {
            val job = scheduler.getJobDetail(jobKey)
            val triggerConfig = TriggerBuildConfig(
                triggerName = triggerName, description = description, job = job,
                jobDataMap = jobDataMap, intervalInSeconds = intervalInSeconds, endAt = endAt
            )
            if (!scheduler.checkExists(triggerKey)) {
                val trigger: SimpleTrigger = buildTriggerObject(triggerConfig)
                triggerJob(job = null, trigger = trigger)
            } else {
                val newTrigger: SimpleTrigger = buildTriggerObject(triggerConfig)
                replaceTrigger(triggerKey, newTrigger)
            }
        }
    }

    /**
     * Function used to build a trigger object
     * Builds different objects based on job provided or not.
     *
     * @param triggerConfig Configuration of trigger
     */
    private fun buildTriggerObject(triggerConfig: TriggerBuildConfig): SimpleTrigger {
        return if (triggerConfig.job != null) {
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerConfig.triggerName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME)
                    .withDescription(triggerConfig.description)
                    .forJob(triggerConfig.job)
                    .usingJobData(triggerConfig.jobDataMap)
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(triggerConfig.intervalInSeconds)
                            .repeatForever()
                    )
                    .endAt(triggerConfig.endAt)
                    .build()
            trigger
        } else {
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerConfig.triggerName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME)
                    .withDescription(triggerConfig.description)
                    .usingJobData(triggerConfig.jobDataMap)
                    .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(triggerConfig.intervalInSeconds)
                            .repeatForever()
                    )
                    .endAt(triggerConfig.endAt)
                    .build()
            trigger
        }
    }


    /**
     * Fetches triggers added to the scheduler based on metric id and database name
     *
     * @param metricId metric id to fetch triggers for required metric
     * @param databaseName name of database to fetch triggers for required database
     */
    fun getAllMonitoringTriggers(
        metricId: String,
        databaseName: String
    ): List<Map<String, Any?>> {
        val group: String = SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME
        val allTriggerKeys =
            scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(group))
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
        return triggerDetailsList
    }

    /**
     * DANGER! To be used to reset Quartz scheduler.
     */
    fun removeEverything() {
        scheduler.clear()
    }

    /**
     * Function responsible to delete an existing trigger in quartz scheduler
     * @param triggerName name of the trigger to be deleted
     */
    fun deleteTrigger(
        triggerName: String
    ): Boolean {
        return try {
            if (scheduler.checkExists(TriggerKey(triggerName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME))) {
                scheduler.unscheduleJob(TriggerKey(triggerName, SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME))
                true
            } else {
                throw TriggerNotFoundException("[deleteTrigger] Trigger with name $triggerName not found")
            }
        } catch (e: TriggerNotFoundException) {
            logger.info("[deleteTrigger] Trigger not found: $e")
            false
        } catch (e: Exception) {
            logger.info("[deleteTrigger] Failed to stop data recording: $e")
            false
        }
    }

    /**
     * Function responsible to reschedule a job (Primararily replace/update an existing trigger)
     *
     * @param triggerKey trigger key of existing trigger
     * @param newTrigger updated trigger object
     */
    private fun replaceTrigger(triggerKey: TriggerKey, newTrigger: SimpleTrigger): Boolean {
        return try {
            scheduler.rescheduleJob(triggerKey, newTrigger)
            logger.info("[replaceTrigger] Trigger updated with TriggerKey: ${newTrigger.key}")
            true
        } catch (e: Exception) {
            logger.error("[replaceTrigger] Trigger replacement failed due to $e", e.stackTrace)
            false
        }
    }

    /**
     * Adds a job and trigger to the scheduler
     *
     * @param job (Optional) Details of the job to be added
     * @param trigger Trigger object to be added
     */
    private fun triggerJob(
        job: JobDetail?,
        trigger: SimpleTrigger
    ): Boolean {
        return try {
            if (job == null) scheduler.scheduleJob(trigger)
            else scheduler.scheduleJob(job, trigger)
            logger.info("[triggerJob] Trigger added with TriggerKey: ${trigger.key}")
            true
        } catch (e: Exception) {
            logger.error("[triggerJob] Job Scheduling failed due to $e", e.stackTrace)
            false
        }
    }
}
