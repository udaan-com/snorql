package com.udaan.snorql.framework.job

import com.fasterxml.jackson.databind.ObjectMapper
import com.udaan.snorql.framework.job.model.JobTriggerConfig
import com.udaan.snorql.framework.job.model.QuartzProperties
import com.udaan.snorql.framework.job.model.RecordingJobConfigOutline
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.sql.Timestamp

object JobManager {

    private var schedulerFactory: StdSchedulerFactory = StdSchedulerFactory(QuartzProperties.prop)
    private var scheduler: Scheduler = schedulerFactory.scheduler

    fun initializeJobScheduler() {
        schedulerFactory = StdSchedulerFactory(QuartzProperties.prop)
        scheduler = schedulerFactory.scheduler
    }

    private val objectMapper: ObjectMapper = SnorqlConstants.objectMapper

    fun startScheduler(): Boolean {
        return try {
            scheduler.start()
            true
        } catch (e: Exception) {
            print("Scheduler start failed due to $e")
            false
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
            val configSuccess: Boolean =
                configureJobAndTrigger<T, O, V>(metricInput, intervalInSeconds, startFrom, endAt)
            configSuccess
        } catch (e: Exception) {
            print("Unable to add data recording: $e")
            false
        }
    }

    private fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> configureJobAndTrigger(
        metricInput: T,
        intervalInSeconds: Int,
        startFrom: Timestamp?,
        endAt: Timestamp?,
    ): Boolean {
        val jobName: String = metricInput.metricId // jobName = metricId (Therefore, for each metric, there is a job
        val triggerName: String = metricInput.metricId.plus("_").plus(metricInput.databaseName)
        val jobDataMap = JobDataMap()
        jobDataMap["metricInput"] = objectMapper.writeValueAsString(metricInput) // gson.toJson(metricInput).toString() // Use Jackson
        jobDataMap["inputClass"] = metricInput::class.java.name
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
//                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                    .endAt(endAt)
                    .build()
            triggerJob(job, trigger)
        } else {
            println("Job already exists with job key $jobKey")
            val job = scheduler.getJobDetail(jobKey)
            if (!scheduler.checkExists(triggerKey)) {
                val trigger: SimpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
                    .forJob(job)
//                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                    .endAt(endAt)
                    .build()
                triggerJob(job = null, trigger = trigger)
            } else {
                val newTrigger: SimpleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, SnorqlConstants.MONITORING_GROUP_NAME)
                    .forJob(job)
//                        .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                    .endAt(endAt)
                    .build()
                replaceTrigger(triggerKey, newTrigger)
            }
        }
    }

    fun getAllMonitoringTriggers(): List<Trigger> {
        val allTriggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup()) // groupEquals(SnorqlConstants.MONITORING_GROUP_NAME))
        val triggersList = mutableListOf<Trigger>()
        allTriggerKeys.forEach { triggerKey ->
            run {
                val trigger = scheduler.getTrigger(triggerKey)
                triggersList.add(trigger)
                val triggerDetailsMap = mapOf<String, String?>(
                    "triggerGroup" to trigger.key.group,
                    "triggerName" to trigger.key.name,
                )
//                println("Trigger: $triggerDetailsMap")
            }
        }
//        println("All triggers printed")
        return triggersList
    }

//    fun removeAllTriggers() {
//        getAllMonitoringTriggers().forEach {
//            scheduler.unscheduleJob(it.key)
//        }
//    }

    fun removeEverything() {
        scheduler.clear()
    }

    fun removeDataRecording(
        triggerName: String
    ): Boolean {
        return try {
            scheduler.unscheduleJob(TriggerKey(triggerName, SnorqlConstants.MONITORING_GROUP_NAME))
            true
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