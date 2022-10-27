package com.udaan.snorql.extensions.session

import com.udaan.snorql.framework.IMtericId

enum class SessionEnums(private val metricId:String): IMtericId {

    /**
     * Session Locks Metric ID mapping
     */
    SESSION_LOCKS("sessionLocks"),

    /**
     * Session Active Query
     */
    SESSION_ACTIVE_QUERY("sessionActiveQuery"),

    /**
     * Session Latest Executed Query
     */
    LATEST_EXECUTED_QUERY("latestExecutedQuery");

    override fun getId(): String {
        return "session_" + this.metricId
    }
}