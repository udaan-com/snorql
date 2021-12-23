package com.udaan.snorql.framework.job

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.udaan.snorql.framework.job.model.JobTriggerConfig
import com.udaan.snorql.framework.job.model.QuartzProperties
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.sql.Timestamp
import java.util.*

class JobManager(
    private val schedulerFactory: StdSchedulerFactory = StdSchedulerFactory(QuartzProperties.prop),
    private val scheduler: Scheduler = schedulerFactory.scheduler,
) {
    companion object {
        private const val MONITORING_GROUP_NAME = "monitoring"
        private val gson: Gson = Gson()
    }

    private val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false).registerKotlinModule()
        }

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
        jobConfig: JobTriggerConfig,
        metricInput: T,
    ): Boolean {
        return try {
            val triggerName: String = UUID.randomUUID().toString()
            val intervalInSeconds = jobConfig.watchIntervalInSeconds
            val startFrom = jobConfig.startFrom
            val endAt = jobConfig.endAt
            val configSuccess: Boolean =
                configureJobAndTrigger<T, O, V>(triggerName, metricInput, intervalInSeconds, startFrom, endAt)
            configSuccess
        } catch (e: Exception) {
            print("Unable to add data recording: $e")
            false
        }
    }

    private fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> configureJobAndTrigger(
        triggerName: String,
        metricInput: T,
        intervalInSeconds: Int,
        startFrom: Timestamp?,
        endAt: Timestamp?,
    ): Boolean {
        val jobName: String = metricInput.metricId // jobName = metricId (Therefore, for each metric, there is a job
        val jobDataMap = JobDataMap()
        jobDataMap["metricInput"] = objectMapper.writeValueAsString(metricInput) // gson.toJson(metricInput).toString() // Use Jackson
        val jobKey = JobKey(jobName, MONITORING_GROUP_NAME)
        return if (!scheduler.checkExists(jobKey)) {
            println("Job does not exist. Configuring a job with job key $jobKey")
            val job = JobBuilder.newJob(SimpleJob<T, O, V>().javaClass)
                .withIdentity(jobName, MONITORING_GROUP_NAME)
                .storeDurably()
                .build()
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, MONITORING_GROUP_NAME)
                    .startAt(startFrom)
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
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, MONITORING_GROUP_NAME)
                    .forJob(job)
                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(intervalInSeconds)
                        .repeatForever())
                    .endAt(endAt)
                    .build()
            triggerJob(job = null, trigger = trigger)
        }
    }

    fun getAllMonitoringJobsAndTriggers(): List<Trigger> {
        val allTriggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup()) // groupEquals(MONITORING_GROUP_NAME))
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

    fun removeDataRecording(
        metricId: String,
        databaseName: String,
        triggerName: String,
    ): Boolean {
        return try {
            scheduler.unscheduleJob(TriggerKey(triggerName, "monitoring"))
            true
        } catch (e: Exception) {
            println("Failed to stop data recording: $e")
            false
        }
    }

    private fun triggerJob(
        job: JobDetail?,
        trigger: SimpleTrigger,
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