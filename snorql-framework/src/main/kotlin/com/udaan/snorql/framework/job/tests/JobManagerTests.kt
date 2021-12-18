package com.udaan.snorql.framework.job.tests

import com.udaan.snorql.framework.job.JobManager
import org.junit.jupiter.api.Test

class JobManagerTests {

    @Test
    fun jobSchedulingTest() {
        val jobManager = JobManager()
        jobManager.startScheduler()
        jobManager.triggerJob()
//        jobManager.triggerJob()
    }
}