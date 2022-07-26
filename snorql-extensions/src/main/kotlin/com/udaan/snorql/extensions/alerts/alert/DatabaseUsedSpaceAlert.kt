package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.models.DatabaseUsedSpaceAlertResult
import com.udaan.snorql.extensions.alerts.models.DatabaseUsedSpaceInput
import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.extensions.storage.models.DbInput
import com.udaan.snorql.extensions.storage.models.DbResult
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

class DatabaseUsedSpaceAlert : IAlert<DatabaseUsedSpaceInput, DatabaseUsedSpaceAlertResult, IAlertRecommendation> {
    override fun getAlertOutput(
        alertInput: DatabaseUsedSpaceInput,
        alertConfig: AlertConfigOutline
    ): AlertOutput<DatabaseUsedSpaceAlertResult, IAlertRecommendation> {
        val queryList = (SqlMetricManager.getMetric<DbInput, DbResult, IMetricRecommendation>(
            metricId = StorageEnums.DB.getId(),
            metricInput = DbInput(
                metricPeriod = MetricPeriod.REAL_TIME,
                databaseName = alertInput.databaseName,
                dbName = alertInput.dbName
            )
        ).metricOutput as MetricOutput<DbResult, IMetricRecommendation>).result.queryList
        val usedSpaceInKB = queryList[0].reserved.split(" ")[0].toFloat()
        val usedSpaceInGB: Float = usedSpaceInKB / 1048576
        val percentageOccupied = usedSpaceInGB * 100 / queryList[0].dbTotalSize

        val isAlert = percentageOccupied >= alertInput.percentageOccupiedThreshold

        return AlertOutput(
            isAlert = isAlert,
            alertResult = DatabaseUsedSpaceAlertResult(
                storageResult = queryList[0]
            ),
            recommendation = null
        )
    }
}