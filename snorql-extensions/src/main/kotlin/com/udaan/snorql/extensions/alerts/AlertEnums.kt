package com.udaan.snorql.extensions.alerts

import com.udaan.snorql.framework.IAlertId

enum class AlertEnums(private val alertId: String): IAlertId {

    ACTIVE_QUERIES_FILTER("activeQueriesFilter"),

    DATABASE_USED_SPACE("databaseUsedSpace"),

    RESOURCE_UTILIZATION("resourceUtilization"),

    INDEX_FRAGMENTATION("indexFragmentation");

    override fun getId(): String {
        return "alert_" + this.alertId
    }


}