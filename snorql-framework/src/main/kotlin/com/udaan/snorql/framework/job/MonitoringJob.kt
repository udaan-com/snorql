package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.job.model.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.SnorqlConstants
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.sql.Timestamp
import java.util.*

class MonitoringJob<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> : Job {

    /**
     * (Deprecated) This function does the following:
     * 1. Gets the trigger name which triggered the job using <code>context.trigger.key.name</code>
     * 2. Once it has the trigger name, search for job configuration in the database.
     *
     * The function does the following:
     * 1. Fetched the MergedJobDataMap, which holds metricId and databaseName.
     * 2. In case the trigger does not has the values, these can be fetched from the database which holds the mapping of
     * triggerName -> metricId, databaseName and metricInput
     * 3. Once it has the parameters, it calls getMetricOutput which returns metricOutput (Result & Recommendation)
     * 4. timestamp, runId, metricId, databaseName, source, metricOutput, metricInput are stored in historical data cluster
     */
    override fun execute(context: JobExecutionContext) {

        // 1. Created MergedJobDataMap Instance
        // 2. Fetch metricId, metricInput from MergedJobDataMap instance
        // 3. Fetch MetricOutput by calling getMetricOutput(metricInput)
        // 4. Generate a run ID using UUID
        // 5. Store historical data in database

        try {
            print("Quartz Job execution started!!")
            val runID: String = UUID.randomUUID().toString() // Generate a random monitoring run id
            val mergedDataMap = context.mergedJobDataMap
            val metricInput: T =
                SnorqlConstants.objectMapper.readValue(mergedDataMap["metricInput"] as String,
                    Class.forName(mergedDataMap["inputClass"] as String)) as T
            val metricResponse = SqlMetricManager.getMetric<T, O, R>(metricInput.metricId, metricInput)
            val metricOutput = metricResponse.metricOutput

            val dataRecorded = HistoricalDatabaseSchemaDTO(
                runId = runID,
                timestamp = Timestamp(System.currentTimeMillis()),
                metricId = metricInput.metricId,
                databaseName = metricInput.databaseName,
                source = SnorqlConstants.MONITORING_GROUP_NAME,
                metricInput = SnorqlConstants.objectMapper.writeValueAsString(metricInput), // metricInput,
                metricOutput = SnorqlConstants.objectMapper.writeValueAsString(metricOutput) // metricOutput
            )
            val storageId = SnorqlConstants.HISTORICAL_DATA_BUCKET_ID
            SqlMetricManager.queryExecutor.persistHistoricalData(storageId, listOf(dataRecorded))
            println("Following data was recorded: $dataRecorded")
            println("Quartz Job execution complete!!")
        } catch (e: Exception) {
            println("Exception: $e")
            println("There was an exception while recording data: ${e.stackTrace}")
            e.printStackTrace()
        }
    }
}