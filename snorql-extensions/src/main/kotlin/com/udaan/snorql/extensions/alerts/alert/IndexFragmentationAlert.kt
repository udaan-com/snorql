package com.udaan.snorql.extensions.alerts.alert

import com.udaan.snorql.extensions.alerts.models.IndexFragmentationAlertInput
import com.udaan.snorql.extensions.alerts.models.IndexFragmentationAlertRecommendation
import com.udaan.snorql.extensions.alerts.models.IndexFragmentationAlertResult
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.models.IndexFragmentationDTO
import com.udaan.snorql.extensions.performance.models.IndexFragmentationInput
import com.udaan.snorql.extensions.performance.models.IndexFragmentationRecommendation
import com.udaan.snorql.extensions.performance.models.IndexFragmentationResult
import com.udaan.snorql.framework.alerts.IAlert
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.AlertConfigOutline
import com.udaan.snorql.framework.models.AlertOutput
import com.udaan.snorql.framework.models.MetricPeriod
import com.udaan.snorql.framework.models.MetricResponse

class IndexFragmentationAlert :
    IAlert<IndexFragmentationAlertInput, IndexFragmentationAlertResult, IndexFragmentationAlertRecommendation> {

    override fun getAlertOutput(alertInput: IndexFragmentationAlertInput, alertConfig: AlertConfigOutline):
            AlertOutput<IndexFragmentationAlertResult, IndexFragmentationAlertRecommendation> {
        val metricResponse =
            SqlMetricManager.getMetric<IndexFragmentationInput, IndexFragmentationResult, IndexFragmentationRecommendation>(
                metricId = PerformanceEnums.INDEX_FRAGMENTATION.getId(),
                metricInput = IndexFragmentationInput(
                    metricId = PerformanceEnums.INDEX_FRAGMENTATION.getId(),
                    metricPeriod = MetricPeriod.REAL_TIME,
                    databaseName = alertInput.databaseName,
                    mode = alertInput.mode
                )
            )
        val queryList: List<IndexFragmentationDTO> =
            (metricResponse as MetricResponse<IndexFragmentationResult, IndexFragmentationRecommendation>).metricOutput.result.queryList
        val filteredQueryList = if (alertInput.pageCountThreshold != null) {
            queryList.filter { idxFrag -> idxFrag.pageCount >= alertInput.pageCountThreshold }
        } else {
            queryList
        }
//        print("[IndexFragmentationAlert][getAlertOutput] Query List: $filteredQueryList")
        val indexesToReorganise: MutableList<IndexFragmentationDTO> = mutableListOf()
        val indexesToRebuild: MutableList<IndexFragmentationDTO> = mutableListOf()
        filteredQueryList.forEach { idxFrag ->
            if (idxFrag.avgFragmentationInPercent > 30.0) {
                indexesToRebuild.add(idxFrag)
            } else if (idxFrag.avgFragmentationInPercent > 10.0) {
                indexesToReorganise.add(idxFrag)
            }
        }
        val isAlert = indexesToRebuild.isNotEmpty() or indexesToReorganise.isNotEmpty()
        return AlertOutput(
            isAlert = isAlert,
            alertResult = IndexFragmentationAlertResult(
                filteredMetricResult = filteredQueryList
            ),
            recommendation = IndexFragmentationAlertRecommendation(
                indexesToReorganise = indexesToReorganise,
                indexesToRebuild = indexesToRebuild
            )
        )
    }
}