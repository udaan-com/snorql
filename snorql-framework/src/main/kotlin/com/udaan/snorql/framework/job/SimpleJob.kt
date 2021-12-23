package com.udaan.snorql.framework.job

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.reflect.TypeToken
import com.udaan.snorql.framework.job.model.HistoricalDatabaseSchemaDTO
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.lang.Exception
import java.util.*

class SimpleJob<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> : Job {
    private val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false).registerKotlinModule()
        }


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
            println(" - Trigger Key: ${context.trigger.key}")
            val metricInput: T =
                objectMapper.readValue(mergedDataMap["metricInput"] as String, Class.forName(mergedDataMap["inputClass"] as String)) as T
//            val metricInput: T = gson.fromJson(mergedDataMap["metricInput"] as String, MetricInput::class.java) as T
//            val metricInput: T = mergedDataMap["metricInput"] as T
            val metricResponse = SqlMetricManager.getMetric<T, O, R>(metricInput.metricId, metricInput)
            val metricOutput = metricResponse.metricOutput

            val dataRecorded = HistoricalDatabaseSchemaDTO(
                runId = runID,
                metricId = metricInput.metricId,
                databaseName = metricInput.databaseName,
                source = "MONITORING_JOB",
                metricInput = metricInput,
                metricOutput = metricOutput
            )
            val storageId = "HISTORICAL_DATA_BUCKET_ID"
//            SqlMetricManager.queryExecutor.persistHistoricalData(storageId, listOf(dataRecorded))
            println("Following data was recorded: $dataRecorded")
            println("Quartz Job execution complete!!")
        } catch (e: Exception) {
            println("There was an exception while recording data: $e")
        }


    }
}