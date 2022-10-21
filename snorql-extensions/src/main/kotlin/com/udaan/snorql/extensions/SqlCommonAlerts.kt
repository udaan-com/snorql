package com.udaan.snorql.extensions

import com.udaan.snorql.extensions.alerts.*
import com.udaan.snorql.extensions.alerts.alert.ActiveQueriesAlert
import com.udaan.snorql.framework.alerts.AlertsManager

object SqlCommonAlerts {
    fun initialize() {
        AlertsManager.addAlertToMap(AlertEnums.ACTIVE_QUERIES_FILTER.getId(), ActiveQueriesAlert())
    }
}