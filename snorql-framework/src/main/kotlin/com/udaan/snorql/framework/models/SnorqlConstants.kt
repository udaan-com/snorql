package com.udaan.snorql.framework.models

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object SnorqlConstants {
    val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).registerKotlinModule()
        }

    const val DATA_PERSISTENCE_GROUP_NAME = "DATA_PERSISTENCE"
    const val ALERT_GROUP_NAME = "SNORQL_ALERTING"
    const val DATA_PURGE_GROUP_NAME = "DATA_PURGE"

    var HISTORICAL_DATA_BUCKET_ID = "sql_monitoring_historical_data"
    val historicalDataTableColumns =
        listOf("runId", "timestamp", "metricId", "databaseName", "source", "metricInput", "metricOutput")
}
