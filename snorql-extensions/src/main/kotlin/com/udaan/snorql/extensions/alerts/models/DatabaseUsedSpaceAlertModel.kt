package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.storage.models.DbStorageSize
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertResult

data class DatabaseUsedSpaceInput(
    override val databaseName: String,
    val dbName: String,
    val percentageOccupiedThreshold: Int
) : AlertInput()

data class DatabaseUsedSpaceAlertResult(
    val storageResult: DbStorageSize
) : IAlertResult()