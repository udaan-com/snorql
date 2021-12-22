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
        SqlMetricManager.addMetric("performance_activeQueries",ActiveQueriesMetric())
        val jobManager = JobManager()
        jobManager.startScheduler()
        val triggerConfig = JobTriggerConfig(
                watchIntervalInSeconds = 1,
                startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
                        LocalDateTime.now())))
                                                 )
        val metricInput = ActualMetricInput(
                metricId = "performance_activeQueries",
                databaseName = "uddevsql/db-test"
                                           )
        jobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig,
                metricInput)

        println("Now printing the triggers")
        jobManager.getAllTriggers()
//        jobManager.triggerJob()
//        jobManager.triggerJob()
    }
}