package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.HistoricalDataPurgeConfig
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Calendar

class DataPurgingJob : Job {
    private val logger = SqlMetricManager.logger

    /**
     * This function will be executed by Data Purge Cron Trigger
     */
    override fun execute(context: JobExecutionContext) {
        logger.info("Starting data purge job at ${Calendar.getInstance().time}")
        val mergedDataMap = context.mergedJobDataMap
        val metricsToPurge: MutableList<HistoricalDataPurgeConfig> = mutableListOf()
        for ((key, value) in mergedDataMap.entries) {
            // Key is of the format metricId$databaseName
            val idsArr = key.split("$").toTypedArray()
            val metricId: String = idsArr[0]
            val databaseName: String = idsArr[1]
            val retentionPeriod: Int = value as Int
            val purgeDataOlderThan: Timestamp =
                Timestamp.valueOf(LocalDateTime.now().minusDays(retentionPeriod.toLong()))
            metricsToPurge.add(
                HistoricalDataPurgeConfig(
                    metricId = metricId,
                    databaseName = databaseName,
                    purgeDataOlderThan = purgeDataOlderThan
                )
            )
        }
        try {
            SqlMetricManager.queryExecutor.purgeHistoricalData(
                SnorqlConstants.HISTORICAL_DATA_BUCKET_ID,
                metricsToPurge
            )
        } catch (e: Exception) {
            logger.error("[DataPurgingJob] Unable to purge historical data for data list: $metricsToPurge", e)
        }
    }
}