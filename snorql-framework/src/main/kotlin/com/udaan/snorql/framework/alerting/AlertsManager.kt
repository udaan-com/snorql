package com.udaan.snorql.framework.alerting

import com.sun.org.glassfish.gmbal.Description
import com.udaan.snorql.framework.job.model.QuartzProperties
import org.quartz.Scheduler
import org.quartz.impl.StdSchedulerFactory

object AlertsManager {
    private var alertSchedulerFactory: StdSchedulerFactory = StdSchedulerFactory(QuartzProperties.prop)
    private var alertScheduler: Scheduler = alertSchedulerFactory.scheduler

    fun initializeAlerts() {
        alertSchedulerFactory = StdSchedulerFactory(QuartzProperties.prop)
        alertScheduler = alertSchedulerFactory.scheduler
    }

    /**
     *
     */
    fun createAnAlert(
        alertName: String,
        alertDescription: String,
        alertType: String,
        alertSeverity: Int,
        alertAfter: Int,
        alertQueryString: String
    ) {

    }

    private fun configureAlertJob(
        jobName: String,
        alertName: String,
        alertDescription: String,
        alertType: String,
        alertSeverity: Int,
        alertQueryString: String
    ) {

    }

}