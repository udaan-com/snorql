package com.udaan.snorql.extensions.performance.metrics

import com.udaan.snorql.extensions.performance.models.IndexFragmentationDTO
import com.udaan.snorql.extensions.performance.models.IndexFragmentationInput
import com.udaan.snorql.extensions.performance.models.IndexFragmentationRecommendation
import com.udaan.snorql.extensions.performance.models.IndexFragmentationResult
import com.udaan.snorql.extensions.performance.models.IndexInfo
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class IndexFragmentationMetric :
    IMetric<IndexFragmentationInput, IndexFragmentationResult, IndexFragmentationRecommendation> {

    companion object {
        private const val REORGANIZE_IDX_FRAG_PERCENT_LOWER_BOUND = 10.0
        private const val REORGANIZE_IDX_FRAG_PERCENT_UPPER_BOUND = 30.0
        private const val REBUILD_IDX_FRAG_PERCENT_LOWER_BOUND = 30
    }

    override fun getMetricResult(
        metricInput: IndexFragmentationInput,
        metricConfig: MetricConfig
    ): IndexFragmentationResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException(
                    "SQL config query [main] not found under config " +
                            "[${metricInput.metricId}]"
                )
        val paramMap = mapOf("modeParam" to metricInput.mode.name)
        val result =
            SqlMetricManager.queryExecutor.execute<IndexFragmentationDTO>(
                metricInput.databaseName,
                query,
                paramMap
            )
        return IndexFragmentationResult(result)
    }

    override fun getMetricRecommendations(
        metricInput: IndexFragmentationInput,
        metricResult: IndexFragmentationResult
    ): IndexFragmentationRecommendation? {
        if (metricInput.recommendationRequired) {
            val indexesToReorganise: MutableList<IndexInfo> = mutableListOf()
            val indexesToRebuild: MutableList<IndexInfo> = mutableListOf()
            metricResult.queryList.forEach { idxFrag ->
                if (idxFrag.avgFragmentationInPercent in
                    REORGANIZE_IDX_FRAG_PERCENT_LOWER_BOUND..REORGANIZE_IDX_FRAG_PERCENT_UPPER_BOUND) {
                    indexesToReorganise.add(
                        IndexInfo(
                            idxFrag.schemaName,
                            idxFrag.objectName,
                            idxFrag.indexName,
                            idxFrag.indexType,
                            idxFrag.pageCount
                        )
                    )
                } else if (idxFrag.avgFragmentationInPercent > REBUILD_IDX_FRAG_PERCENT_LOWER_BOUND) {
                    indexesToRebuild.add(
                        IndexInfo(
                            idxFrag.schemaName,
                            idxFrag.objectName,
                            idxFrag.indexName,
                            idxFrag.indexType,
                            idxFrag.pageCount
                        )
                    )
                }
            }
            return IndexFragmentationRecommendation(
                indexesToRebuild = indexesToRebuild,
                indexesToReorganise = indexesToReorganise
            )
        }
        return null
    }

    override fun getMetricResponseMetadata(
        metricInput: IndexFragmentationInput,
        metricOutput: MetricOutput<IndexFragmentationResult, IndexFragmentationRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val metricConfig = getMetricConfig(metricInput.metricId)
        val query = metricConfig.queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        responseMetadata["referenceDocumentation"] = metricConfig.referenceDoc
        responseMetadata["description"] = metricConfig.description
        responseMetadata["supportsHistorical"] = metricConfig.supportsHistorical
        return responseMetadata
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }
}
