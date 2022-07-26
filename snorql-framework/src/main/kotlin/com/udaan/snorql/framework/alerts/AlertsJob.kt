package com.udaan.snorql.framework.alerts

import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*
import org.quartz.Job
import org.quartz.JobExecutionContext

class AlertsJob<in T : AlertInput, O : IAlertResult, V : IAlertRecommendation> : Job {
    private val logger = SqlMetricManager.logger

    override fun execute(context: JobExecutionContext) {
        try {
//            logger.info("Inside AlertsJob Class")
            val mergedDataMap = context.mergedJobDataMap
            val alertInput: T = SnorqlConstants.objectMapper.readValue(
                mergedDataMap["alertInput"] as String,
                Class.forName(mergedDataMap["alertInputClass"] as String)
            ) as T
            val alertType = mergedDataMap["alertType"] as String
            logger.info("[AlertsJob] Alert Input: {}", alertInput)
            val alertConfig = getAlertConfigOutline(context)
            val alertClass = mergedDataMap["alertClass"]
            logger.info("[AlertsJob] Alert Class is: $alertClass")

            val alertResponse = AlertsManager.getAlertResponse<T, O, V>(alertType, alertInput, alertConfig)

            logger.info("[AlertsJob] Alert Response: {}", alertResponse)

            if (alertResponse.alertOutput.isAlert) {
                SqlMetricManager.queryExecutor.handleAlert(
                    alertConfig = alertConfig,
                    alertInput = alertInput,
                    alertOutput = alertResponse.alertOutput
                )
            } else {
                logger.info("[AlertsJob] Not an alert \n{} \n{}", alertInput, alertResponse)
            }
        } catch (e: Exception) {
            logger.error("[AlertsJob] There has been an error", e)
        }
    }

    private fun getAlertConfigOutline(context: JobExecutionContext): AlertConfigOutline {
        return AlertConfigOutline(
            alertType = context.mergedJobDataMap["alertType"] as String,
            alertNameString = context.mergedJobDataMap["alertNameString"] as String,
            description = context.trigger.description,
            severity = context.mergedJobDataMap["severity"] as AlertSeverity,
            configuredByName = context.mergedJobDataMap["configuredByName"] as String,
            configuredByEmail = context.mergedJobDataMap["configuredByEmail"] as String,
            watchIntervalInSeconds = context.mergedJobDataMap["watchIntervalInSeconds"] as Int,
            endAt = null
        )
    }
}