package com.udaan.snorql.framework.job

import com.sun.org.apache.xpath.internal.operations.Bool
import com.udaan.snorql.framework.job.model.JobConfig
import com.udaan.snorql.framework.models.MetricInput
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.lang.Exception

class JobManager(
    val schedFact: StdSchedulerFactory = StdSchedulerFactory(),
    val sched: Scheduler = schedFact.getScheduler(),
) {

    fun startScheduler(): Boolean {
        return try {
            sched.start()
            true
        } catch (e: Exception) {
            print("Scheduler start failed due to $e")
            false
        }
    }

    /**
     * [configureDataRecording] does the following:
     * 1. Configures a trigger with record data job and gets the trigger id
     * 2. If successful, Saves the following in the database: triggerId, metricId, databaseName, metricInput
     * 3. Returns true if successful
     */
//    fun configureDataRecording(metricInput: MetricInput, jobConfig: JobConfig): Boolean {
//
//    }

    private val job: JobDetail = JobBuilder.newJob(SimpleJob::class.java)
        .withIdentity("myjob", "group1")
        .build()
    val trigger: SimpleTrigger = TriggerBuilder.newTrigger()
        .withIdentity("myTrigger", "group1")
        .startNow()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(1)
            .withRepeatCount(5))
        .build()

    fun triggerJob(): Boolean {
        return try {
            sched.scheduleJob(job, trigger)
            true
        } catch (e: Exception) {
            print("Job Scheduling failed due to $e")
            false
        }
    }
}