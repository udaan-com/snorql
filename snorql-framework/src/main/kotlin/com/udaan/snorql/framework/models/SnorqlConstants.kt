package com.udaan.snorql.framework.models

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object SnorqlConstants {
    val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false).registerKotlinModule()
        }

    const val MONITORING_GROUP_NAME = "monitoring"

    const val HISTORICAL_DATA_BUCKET_ID = "sql_monitoring_historical_data"
    val historicalDataTableColumns =
        listOf("runId", "timestamp", "metricId", "databaseName", "source", "metricInput", "metricOutput")
}