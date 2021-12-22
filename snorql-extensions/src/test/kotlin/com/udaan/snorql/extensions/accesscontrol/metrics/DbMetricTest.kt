package com.udaan.snorql.extensions.accesscontrol.metrics

import com.udaan.snorql.extensions.performance.models.LongRunningInput
import com.udaan.snorql.extensions.storage.metrics.DbMetric
import com.udaan.snorql.extensions.storage.models.DbDTO
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.framework.models.MetricPeriod

class DbMetricTest {
    companion object {
        private val dbMetric = DbMetric()
    }

    private val dbMetricMainQuery: String? = dbMetric.getMetricConfig(
        DbInput(metricPeriod = MetricPeriod.HISTORICAL,
            databaseName = "randomDatabaseName", dbName = "randomDBName").metricId).queries["main"]

    // Database Stats Metrics
    private val dbMetric1 = DbDTO(databaseName = "randomDatabaseName1",
        databaseSize = "260MB",
        unallocatedSpace = "70MB",
        reserved = "180MB",
        data = "45MB",
        indexSize = "17MB",
        unused = "44MB")
    private val dbMetric2 = DbDTO(databaseName = "randomDatabaseName2",
        databaseSize = "26MB",
        unallocatedSpace = "7MB",
        reserved = "18MB",
        data = "4.5MB",
        indexSize = "1.7MB",
        unused = "4.4MB")
}