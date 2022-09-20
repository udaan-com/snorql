package com.udaan.snorql.framework.models

import java.sql.Timestamp

data class AlertConfigOutline(
    val alertType: String,
    val alertNameString: String,
    val description: String?,
    val severity: AlertSeverity,
    val configuredByName: String?,
    val configuredByEmail: String?,
    val watchIntervalInSeconds: Int,
    val endAt: Timestamp?
)

/**
 * Abstract alert input class
 */
abstract class AlertInput {
    abstract val databaseName: String
}

/**
 * Alert Output data class
 */
data class AlertOutput<T : IAlertResult, V : IAlertRecommendation>(
    val isAlert: Boolean,
    val alertResult: T,
    val recommendation: V?
)

enum class AlertSeverity {
    CRITICAL, ERROR, WARNING, INFORMATIONAL, VERBOSE
}

/**
 * Response of an alert
 */
data class AlertResponse<T : IAlertResult, V : IAlertRecommendation>(
    val alertInput: AlertInput, val alertOutput: AlertOutput<*, *>
)

/**
 * Abstract alert result class
 */
abstract class IAlertResult

/**
 * Alert recommendation
 */
abstract class IAlertRecommendation
