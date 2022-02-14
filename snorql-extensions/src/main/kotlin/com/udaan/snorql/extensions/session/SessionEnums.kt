package com.udaan.snorql.extensions.session

import com.udaan.snorql.framework.IMtericId

enum class SessionEnums(private val metricId:String): IMtericId {

    /*
    Session Locks Metric ID mapping
     */
    SESSION_LOCKS("sessionLocks");

    override fun getId(): String {
        return "session_" + this.metricId
    }
}