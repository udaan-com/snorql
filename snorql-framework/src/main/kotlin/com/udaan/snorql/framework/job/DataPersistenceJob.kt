package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.models.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.metric.logger
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.sql.Timestamp
import java.util.UUID

class DataPersistenceJob<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> : Job {
    val logger by logger()

    /**
     * Function executed when a data recording trigger is fired
     * The function does the following:
     * 1. Generates a random runID
     * 2. Receives merged data map (contains data required for execution)
     * 3. Fetches metric data
     * 4. Calls the function to store data in historical data store
     */
    override fun execute(context: JobExecutionContext) {
        try {
            print("Quartz Job execution started!!")
            val runID: String = UUID.randomUUID().toString()
            val mergedDataMap = context.mergedJobDataMap
            val metricInput: T =
                SnorqlConstants.objectMapper.readValue(
                    mergedDataMap["metricInput"] as String,
                    Class.forName(mergedDataMap["inputClass"] as String)
                ) as T
            val metricResponse = SqlMetricManager.getMetric<T, O, R>(metricInput.metricId, metricInput)
            val metricOutput = metricResponse.metricOutput

            val dataRecorded = HistoricalDatabaseSchemaDTO(
                runId = runID,
                timestamp = Timestamp(System.currentTimeMillis()),
                metricId = metricInput.metricId,
                databaseName = metricInput.databaseName,
                source = SnorqlConstants.DATA_PERSISTENCE_GROUP_NAME,
                metricInput = SnorqlConstants.objectMapper.writeValueAsString(metricInput), // metricInput,
                metricOutput = SnorqlConstants.objectMapper.writeValueAsString(metricOutput) // metricOutput
            )
            val storageId = SnorqlConstants.HISTORICAL_DATA_BUCKET_ID
            SqlMetricManager.queryExecutor.persistHistoricalData(storageId, listOf(dataRecorded))
//            println("Following data was recorded: $dataRecorded")
//            println("Quartz Job execution complete!!")
        } catch (e: Exception) {
            logger.error("[DataPersistenceJob] $e", e.stackTraceToString())
//            println("Exception: $e")
//            println("There was an exception while recording data: ${e.stackTrace}")
//            e.printStackTrace()
        }
    }
}
