package com.udaan.snorql.extensions.alerts.models

import com.udaan.snorql.extensions.performance.models.IndexFragmentationDTO
import com.udaan.snorql.extensions.performance.models.IndexPhysicalStatsModes
import com.udaan.snorql.framework.models.AlertInput
import com.udaan.snorql.framework.models.IAlertRecommendation
import com.udaan.snorql.framework.models.IAlertResult

data class IndexFragmentationAlertInput(
    override val databaseName: String,
    val mode: IndexPhysicalStatsModes = IndexPhysicalStatsModes.LIMITED,
    val pageCountThreshold: Int?
) : AlertInput()

data class IndexFragmentationAlertResult(
    val filteredMetricResult: List<IndexFragmentationDTO>
) : IAlertResult()

data class IndexFragmentationAlertRecommendation(
    val indexesToReorganise: List<IndexFragmentationDTO>,
    val indexesToRebuild: List<IndexFragmentationDTO>
) : IAlertRecommendation()