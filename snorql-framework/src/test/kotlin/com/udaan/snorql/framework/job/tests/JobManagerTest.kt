/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework.job.tests

import com.nhaarman.mockitokotlin2.mock
import com.udaan.snorql.framework.job.JobManager
import com.udaan.snorql.framework.job.model.*
import com.udaan.snorql.framework.metric.Connection
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.SnorqlConstants
import org.junit.Test
import resources.QuartzProperties

class JobManagerTests {

    private val jobManager = JobManager
//
//    @Test
//    fun jobSchedulingTest() {}
//        SqlMetricManager.addMetric("performance_activeQueries", ActiveQueriesMetric())
//        val mockConnection: Connection = mock()
//        SqlMetricManager.setConnection(mockConnection)
////        jobManager.startScheduler()
//        val triggerConfig1 = JobTriggerConfig(
//            watchIntervalInSeconds = 2,
//            startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
//                LocalDateTime.now())))
//        )
//        val triggerConfig2 = JobTriggerConfig(
//            watchIntervalInSeconds = 3,
//            startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
//                LocalDateTime.now())))
//        )
//        val triggerConfig3 = JobTriggerConfig(
//            watchIntervalInSeconds = 6,
//            startFrom = Timestamp.from(LocalDateTime.now().toInstant(ZoneId.systemDefault().rules.getOffset(
//                LocalDateTime.now())))
//        )
//        val metricInput1 = ActualMetricInput(
//            metricId = "performance_activeQueries",
//            databaseName = "uddevsql/db-test"
//        )
//        val metricInput2 = ActualMetricInput(
//            metricId = "performance_activeQueries",
//            databaseName = "uddevsql/db-dev"
//        )
//        val metricInput3 = ActualMetricInput(
//            metricId = "performance_activeQueries",
//            databaseName = "uddevsql/db-test"
//        )

//        whenever(SqlMetricManager.queryExecutor.persistHistoricalData(anyString(), any())).thenAnswer{
//            val storageId: String = it.arguments[0] as String
//            val historicalDataDTOList: Any! = it.arguments[1]
//
//        }

//        val mockQueryExecutor = Mockito.mock(QueryExecutor::class.java)
//        val mockQueryExecutor: QueryExecutor = mock()
//        val mockManager: SqlMetricManager = mock()
//        whenever(mockManager.queryExecutor.persistHistoricalData(anyString(), any())).thenAnswer {
//            println("Following data has been saved in storageBucketId ${it.arguments[0]}\n${it.arguments[1]}")
//        }
//        jobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig1,
//            metricInput1)
//        jobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig2,
//            metricInput2)
//        jobManager.addJob<ActualMetricInput, ActualMetricOutput, IMetricRecommendation>(triggerConfig3,
//            metricInput1)
//        println("Printing the triggers configured...")
//        jobManager.getAllMonitoringTriggers().forEach {
//            println(it)
//        }
//        while (true) {
//            Thread.sleep(20_000)
//        }
//        jobManager.triggerJob()
//        jobManager.triggerJob()
//    }

//    @Test // Used to remove all the triggers
//    fun removeAllTriggers() {
//        jobManager.startScheduler()
//        jobManager.removeAllTriggers()
//    }

    @Test
    fun testJobManagerFunctions() {
        SqlMetricManager.addMetric("performance_activeQueries", ActiveQueriesMetric())
        val mockConnection: Connection = mock()
        SqlMetricManager.setConnection(mockConnection)
        jobManager.initializeJobScheduler(QuartzProperties.prop)
//        jobManager.getAllMonitoringTriggers(SnorqlConstants.MONITORING_GROUP_NAME, "performance_activeQueries","uddevsql/db-test")
    }


    @Test // Used to remove all the triggers
    fun removeEverything() {
        JobManager.initializeJobScheduler(QuartzProperties.prop)
        JobManager.removeEverything()
    }
}