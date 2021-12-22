package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.job.model.JobTriggerConfig
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.SimpleTrigger
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.impl.StdSchedulerFactory
import org.quartz.impl.matchers.GroupMatcher
import java.sql.Timestamp
import java.util.*

class JobManager(
        private val schedulerFactory: StdSchedulerFactory = StdSchedulerFactory(),
        private val scheduler: Scheduler = schedulerFactory.scheduler,
                ) {

//    private val queryExecutor = SqlMetricManager.queryExecutor



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
    fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> addJob(jobConfig: JobTriggerConfig,
            metricInput: T): Boolean {
        return try {
            // Transaction Start
            val triggerName: String = UUID.randomUUID().toString()
            val intervalInSeconds = jobConfig.watchIntervalInSeconds
            val startFrom = jobConfig.startFrom
            val endAt = jobConfig.endAt
            val configSuccess: Boolean =
                configureTrigger<T, O, V>(triggerName,
                        metricInput,
                        intervalInSeconds,
                        startFrom,
                        endAt)
            val recordSaveSuccess: Boolean = true
//                queryExecutor.persistJobConfigData(metricInput.metricId, metricInput.databaseName, triggerName)
            if (configSuccess or recordSaveSuccess) {
                // Transation Commit
                println("Data recording configured")
            } else {
                // Transaction Rollback
                println("Data recording configuration failed")
            }
            configSuccess or recordSaveSuccess
        } catch (e: Exception) {
            // Transaction Rollback
            print("Unable to add data recording: $e")
            false
        }
    }

    private fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> configureTrigger(
            triggerName: String,
            metricInput: T,
            intervalInSeconds: Int,
            startFrom: Timestamp?,
            endAt: Timestamp?,
                                                                                                ): Boolean {
        var metricResult: O? = null
        var metricRecommendation: V? = null
        val jobDataMap = JobDataMap()
        jobDataMap["metricInputObject"] = metricInput
        jobDataMap["metricResultObject"] = metricResult
        jobDataMap["metricRecommendationObject"] = metricRecommendation
        val trigger: SimpleTrigger =
            TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, "monitoring")
                    .startAt(startFrom)
                    .usingJobData(jobDataMap)
                    // .usingJobData("metricInput", metricInput.toJsonString()) // Converting metricInput to JSONString
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(intervalInSeconds))
                    .endAt(endAt)
                    .build()

        val job= JobBuilder.newJob(SimpleJob<T,O,V>().javaClass)
                .withIdentity("watcherJob", "monitoring")
                .storeDurably()
                .build()
        return triggerJob(job, trigger)
    }

    fun getAllTriggers(jobId: String = "watcherJob") {
        val allTriggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyGroup())
        allTriggerKeys.forEach { triggerKey ->
            run {
                println(scheduler.getTrigger(triggerKey))
                println("Trigger Data Map: Metric Input - ${
                    scheduler.getTrigger(triggerKey).jobDataMap["metricInputObject"]
                }")
            }
        }
        print("All triggers printed")
    }

    fun removeDataRecording(metricId: String,
            databaseName: String,
            triggerName: String): Boolean {
        return try {
            // Transaction start
//            queryExecutor.removeFromDatabase(metricId, databaseName, triggerName)
            scheduler.unscheduleJob(TriggerKey(triggerName, "monitoring"))
            // Transaction Commit
            true
        } catch (e: Exception) {
            // Transaction Rollback
            println("Failed to stop data recording: $e")
            false
        }
    }

    private fun triggerJob(job: JobDetail,
            trigger: SimpleTrigger): Boolean {
        return try {
            scheduler.scheduleJob(job, trigger)
            true
        } catch (e: Exception) {
            print("Job Scheduling failed due to $e")
            false
        }
    }
}