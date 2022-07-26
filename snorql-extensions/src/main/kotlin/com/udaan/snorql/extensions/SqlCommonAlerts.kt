package com.udaan.snorql.extensions

import com.udaan.snorql.extensions.alerts.*
import com.udaan.snorql.extensions.alerts.alert.ActiveQueriesAlert
import com.udaan.snorql.extensions.alerts.alert.DatabaseUsedSpaceAlert
import com.udaan.snorql.extensions.alerts.alert.IndexFragmentationAlert
import com.udaan.snorql.extensions.alerts.alert.ResourceUtilizationAlert
import com.udaan.snorql.framework.alerts.AlertsManager

object SqlCommonAlerts {
    fun initialize() {
        AlertsManager.addAlertToMap(AlertEnums.ACTIVE_QUERIES_FILTER.getId(), ActiveQueriesAlert())
        AlertsManager.addAlertToMap(AlertEnums.DATABASE_USED_SPACE.getId(), DatabaseUsedSpaceAlert())
        AlertsManager.addAlertToMap(AlertEnums.INDEX_FRAGMENTATION.getId(), IndexFragmentationAlert())
        AlertsManager.addAlertToMap(AlertEnums.RESOURCE_UTILIZATION.getId(), ResourceUtilizationAlert())
    }
}