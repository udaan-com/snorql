package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.JobNotFoundException
import com.udaan.snorql.framework.TriggerNotFoundException
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.CronTriggerBuildConfig
import com.udaan.snorql.framework.models.SnorqlConstants
import com.udaan.snorql.framework.models.TriggerBuildConfig
import org.quartz.CronExpression
import org.quartz.CronScheduleBuilder
import org.quartz.CronTrigger
import org.quartz.JobBuilder
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.SimpleTrigger
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.quartz.impl.StdSchedulerFactory
import java.util.Properties

object QuartzUtils {

    private var schedulerFactory: StdSchedulerFactory = StdSchedulerFactory()
    var scheduler: Scheduler = schedulerFactory.scheduler
    private val logger = SqlMetricManager.logger

    /**
     * Function to initialize Quartz Job Scheduler
     *
     * To be called by the user to initialize Job Scheduling in snorql
     *
     * @param snorqlProperties Quartz properties as required by the user. Also defines the Quartz Job Store
     */
    fun initializeJobScheduler(snorqlProperties: Properties?) {
        schedulerFactory = if (snorqlProperties != null) StdSchedulerFactory(snorqlProperties)
        else StdSchedulerFactory()
        scheduler = schedulerFactory.scheduler

        if ((snorqlProperties != null) && snorqlProperties.containsKey("HISTORICAL_DATA_BUCKET_ID")) {
            logger.info("Setting Historical Bucket ID to: ${snorqlProperties["HISTORICAL_DATA_BUCKET_ID"]}")
            SnorqlConstants.HISTORICAL_DATA_BUCKET_ID = snorqlProperties["HISTORICAL_DATA_BUCKET_ID"] as String
        }
        scheduler.start()
        logger.info("[QuartzUtils] Quartz scheduler started")
        createDataPurgeTriggerIfAbsent()
    }

    /**
     * Function responsible to add into JobDataMap of a job
     * @param jobKey JobKey of job in context
     * @param data Data to be added into JobDataMap of job
     */
    fun addDataIntoJobData(jobKey: JobKey, data: Map<String, Int>) {
        if (scheduler.checkExists(jobKey)) {
            val job = scheduler.getJobDetail(jobKey)
            for ((key, value) in data.entries) {
                job.jobDataMap[key] = value
            }
            scheduler.addJob(job, true)
        } else {
            throw JobNotFoundException("[QuartzUtils][addDataIntoJobData] Job $jobKey does not exist!")
        }
    }

    /**
     * Function responsible to remove data from a job
     * @param jobKey JobKey of job in context
     * @param keysToRemove List of keys to be removed from JobDataMap of the job
     */
    fun removeFromJobData(jobKey: JobKey, keysToRemove: List<String>) {
        if (scheduler.checkExists(jobKey)) {
            val job = scheduler.getJobDetail(jobKey)
            keysToRemove.forEach {
                job.jobDataMap.remove(it)
            }
            scheduler.addJob(job, true)
        } else {
            throw JobNotFoundException("[QuartzUtils][removeFromJobData] Job $jobKey does not exist!")
        }
    }

    /**
     * Function responsible to reschedule a job (Primarily replace/update an existing trigger)
     *
     * @param triggerKey trigger key of existing trigger
     * @param newTrigger updated trigger object
     */
    fun replaceTrigger(triggerKey: TriggerKey, newTrigger: Trigger): Boolean {
        return try {
            scheduler.rescheduleJob(triggerKey, newTrigger)
            logger.info("[QuartzUtils][replaceTrigger] Trigger updated with TriggerKey: ${newTrigger.key}")
            true
        } catch (e: Exception) {
            logger.error("[QuartzUtils][replaceTrigger] Trigger replacement failed due to $e", e.stackTrace)
            false
        }
    }

    /**
     * Adds a job and trigger to the scheduler
     *
     * @param job (Optional) Details of the job to be added
     * @param trigger Trigger object to be added
     */
    fun triggerJob(
        job: JobDetail?,
        trigger: Trigger
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

    /**
     * Function used to build a Cron Trigger Object
     * Builds different objects based on job is provided or not
     *
     * @param triggerConfig Cron Trigger Configuration
     * @param triggerGroup Group that the cron trigger will belong to
     */
    fun buildCronTriggerObject(
        triggerConfig: CronTriggerBuildConfig,
        triggerGroup: String = SnorqlConstants.DATA_PURGE_GROUP_NAME
    ): CronTrigger {
        return if (triggerConfig.job != null) {
            val trigger: CronTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerConfig.triggerName, triggerGroup)
                    .withDescription(triggerConfig.description)
                    .forJob(triggerConfig.job)
                    .usingJobData(triggerConfig.jobDataMap)
                    .withSchedule(
                        CronScheduleBuilder.cronSchedule(
                            triggerConfig.cronExpression
                        )
                    )
                    .endAt(triggerConfig.endAt)
                    .build()
            trigger
        } else {
            val trigger: CronTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerConfig.triggerName, triggerGroup)
                    .withDescription(triggerConfig.description)
                    .usingJobData(triggerConfig.jobDataMap)
                    .withSchedule(
                        CronScheduleBuilder.cronSchedule(
                            triggerConfig.cronExpression
                        )
                    )
                    .endAt(triggerConfig.endAt)
                    .build()
            trigger
        }
    }

    /**
     * Function used to build a Simple trigger object
     * Builds different objects based on job provided or not.
     *
     * @param triggerConfig Configuration of trigger
     * @param triggerGroup Group that the Simple Trigger will belong to
     */
    fun buildSimpleTriggerObject(
        triggerConfig: TriggerBuildConfig,
        triggerGroup: String = SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME
    ): SimpleTrigger {
        return if (triggerConfig.job != null) {
            val trigger: SimpleTrigger =
                TriggerBuilder.newTrigger()
                    .withIdentity(triggerConfig.triggerName, triggerGroup)
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
                    .withIdentity(triggerConfig.triggerName, triggerGroup)
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

    fun deleteTrigger(triggerName: String, triggerGroup: String): Boolean {
        return if (scheduler.checkExists(TriggerKey(triggerName, triggerGroup))) {
            scheduler.unscheduleJob(TriggerKey(triggerName, triggerGroup))
            true
        } else {
            throw TriggerNotFoundException("[deleteTrigger] Trigger with name $triggerName in group " +
                    "$triggerGroup not found")
        }
    }

    /**
     * Function responsible for creating a historical data purge job and trigger if it is absent
     */
    private fun createDataPurgeTriggerIfAbsent() {
        val jobKey = JobKey("HistoricalDataPurgeJob", SnorqlConstants.DATA_PURGE_GROUP_NAME)
        if (scheduler.checkExists(jobKey)) {
            val job = scheduler.getJobDetail(jobKey)
            val cronTriggerConfig = CronTriggerBuildConfig(
                triggerName = "HistoricalDataPurge",
                description = "Cron Trigger to purge persisted data",
                cronExpression = CronExpression("0 0 0 * * ?"),
                job = job, jobDataMap = JobDataMap(mapOf<String, Int>()), endAt = null
            )
            val newCronTrigger = buildCronTriggerObject(cronTriggerConfig)
            if (!scheduler.checkExists(newCronTrigger.key)) {
                triggerJob(null, newCronTrigger)
            }
        } else {
            val job = JobBuilder.newJob(DataPurgingJob().javaClass).withIdentity(jobKey).storeDurably().build()
            val cronTriggerConfig = CronTriggerBuildConfig(
                triggerName = "HistoricalDataPurge",
                description = "Cron Trigger to purge persisted data",
                cronExpression = CronExpression("0 0 0 * * ?"),
                job = null, jobDataMap = JobDataMap(mapOf<String, Int>()), endAt = null
            )
            val newCronTrigger = buildCronTriggerObject(cronTriggerConfig)
            if (scheduler.checkExists(newCronTrigger.key)) {
                replaceTrigger(newCronTrigger.key, newCronTrigger)
            } else {
                triggerJob(job, newCronTrigger)
            }
        }
        logger.info("[QuartzUtils][createDataPurgeTriggerIfAbsent] Added Data Purge Trigger to scheduler")
    }
}
