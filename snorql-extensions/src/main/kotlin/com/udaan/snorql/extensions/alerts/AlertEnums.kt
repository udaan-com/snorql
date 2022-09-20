package com.udaan.snorql.extensions.alerts

import com.udaan.snorql.framework.IAlertId

enum class AlertEnums(private val alertId: String): IAlertId {

    ACTIVE_QUERIES_FILTER("activeQueriesFilter"),

    DATABASE_USED_SPACE("databaseUsedSpace"),

    RESOURCE_UTILIZATION("resourceUtilization"),

    INDEX_FRAGMENTATION("indexFragmentation"),

    READ_REPLICATION_LAG("readReplicationLag"),

    LOG_SPACE_USAGE("logSpaceUsage"),

    GEO_REPLICA_LAG("geoReplicaLag"),;

    override fun getId(): String {
        return "alert_" + this.alertId
    }


}