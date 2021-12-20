package com.udaan.snorql.framework.job

import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext

class SimpleJob :  Job {
    /**
     * (Deprecated) This function does the following:
     * 1. Gets the trigger name which triggered the job using <code>context.trigger.key.name</code>
     * 2. Once it has the trigger name, search for job configuration in the database.
     *
     * The function does the following:
     * 1. Fetched the MergedJobDataMap, which holds metricId and databaseName.
     * 2. Incase the trigger does not has the values, these can be fetched from the database which holds the mapping of
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

        val dataMap: JobDataMap = context.trigger.jobDataMap

        // Get metric Input
        val metricInput = dataMap["metricInput"]

        val metricInputObject: MetricInput = dataMap["metricInputObject"] as MetricInput


        // Once Metric Input is available, getMetricResponse/Result
        // We need to set up metric factories which can give us metric classes
//        val metricResponse = SqlMetricManager.getMetric<>

        // Once we have the response, persist data
        // val queryExecutor = SqlMetricManager.queryExecutor
        // queryExecutor.persistData(databaseName, tableName, columns, rows)

//        println("Trigger data map from inside of Job: $metricInput")
        println("mertric input object: $metricInputObject")
        println()
        println("This is quartz job which got executed")
    }
}