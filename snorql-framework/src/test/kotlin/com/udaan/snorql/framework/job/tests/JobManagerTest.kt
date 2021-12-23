package com.udaan.snorql.framework.job.tests

import com.udaan.snorql.framework.job.JobManager
import com.udaan.snorql.framework.job.model.ActiveQueriesMetric
import com.udaan.snorql.framework.job.model.ActualMetricInput
import com.udaan.snorql.framework.job.model.ActualMetricOutput
import com.udaan.snorql.framework.job.model.JobTriggerConfig
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricRecommendation
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

class JobManagerTests {

    @Test
    fun jobSchedulingTest() {
        SqlMetricManager.addMetric("performance_activeQueries", ActiveQueriesMetric())

        val triggerConfig1 = JobTriggerConfig(
            watchIntervalInSeconds = 1,
            startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
                LocalDateTime.now())))
        )
        val triggerConfig2 = JobTriggerConfig(
            watchIntervalInSeconds = 3,
            startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
                LocalDateTime.now())))
        )
        val metricInput1 = ActualMetricInput(
            metricId = "performance_activeQueries",
            databaseName = "uddevsql/db-test"
        )
        val metricInput2 = ActualMetricInput(
            metricId = "performance_activeQueries",
            databaseName = "uddevsql/db-dev"
        )
        val metricInput3 = ActualMetricInput(
            metricId = "performance_activeQueries",
            databaseName = "uddevsql/db-test"
        )
//        val mockQueryExecutor = Mockito.mock(QueryExecutor::class.java)
//        val mockQueryExecutor: QueryExecutor = mock()
//        val mockManager: SqlMetricManager = mock()
//        whenever(mockManager.queryExecutor.persistHistoricalData(anyString(), any())).thenAnswer {
//            println("Following data has been saved in storageBucketId ${it.arguments[0]}\n${it.arguments[1]}")
//        }
        JobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig1,
            metricInput1)
        JobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig2,
            metricInput2)
        JobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig2,
            metricInput1)
        println("Printing the triggers configured...")
        JobManager.getAllMonitoringJobsAndTriggers().forEach {
            println(it)
        }

        JobManager.startScheduler()
        while (true) {
            Thread.sleep(20_000)
        }
        //JobManager.triggerJob()
//        JobManager.triggerJob()
    }
}