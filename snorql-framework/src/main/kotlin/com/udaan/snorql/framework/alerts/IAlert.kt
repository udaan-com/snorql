package com.udaan.snorql.framework.alerts

import com.udaan.snorql.framework.models.*

interface IAlert<in T: AlertInput, O: IAlertResult, V: IAlertRecommendation> {

    fun getAlertRecommendation(alertResult: O): V? {
        return null
    }

    fun getAlertOutput(alertInput: T, alertConfig: AlertConfigOutline): AlertOutput<O, V>

    fun getAlertResponse(alertInput: T, alertConfig: AlertConfigOutline): AlertResponse<*, *> {
        val alertOutput = getAlertOutput(alertInput, alertConfig)
        return AlertResponse<O, V>(
            alertInput = alertInput,
            alertOutput = alertOutput
        )
    }
}