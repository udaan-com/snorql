package com.udaan.snorql.extensions.alerts

import com.udaan.snorql.framework.IAlertId

enum class AlertEnums(private val alertId: String): IAlertId {

    ACTIVE_QUERIES_FILTER("activeQueriesFilter");

    override fun getId(): String {
        return "alert_" + this.alertId
    }


}