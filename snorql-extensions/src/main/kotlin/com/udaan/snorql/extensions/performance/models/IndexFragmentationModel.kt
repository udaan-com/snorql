package com.udaan.snorql.extensions.performance.models

import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

data class IndexFragmentationDTO(
    @ColumnName("schema_name")
    val schemaName: String,
    @ColumnName("object_name")
    val objectName: String,
    @ColumnName("index_name")
    val indexName: String?,
    @ColumnName("index_type")
    val indexType: String,
    @ColumnName("avg_fragmentation_in_percent")
    val avgFragmentationInPercent: Float,
    @ColumnName("avg_page_space_used_in_percent")
    val avgPageSpaceUsedInPercent: Float?,
    @ColumnName("page_count")
    val pageCount: Int,
    @ColumnName("alloc_unit_type_desc")
    val allocUnitTypeDesc: String
)

data class IndexFragmentationInput(
    override val metricId: String = PerformanceEnums.INDEX_FRAGMENTATION.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    override val recommendationRequired: Boolean = true,
    val mode: IndexPhysicalStatsModes = IndexPhysicalStatsModes.LIMITED
) : MetricInput()

enum class IndexPhysicalStatsModes {
    DEFAULT, NULL, LIMITED, SAMPLED, DETAILED
}

data class IndexInfo(
    val schemaName: String,
    val objectName: String,
    val indexName: String?,
    val indexType: String,
    val pageCount: Int
)

data class IndexFragmentationResult(val queryList: List<IndexFragmentationDTO>) : IMetricResult()

data class IndexFragmentationRecommendation(
    val indexesToRebuild: List<IndexInfo>,
    val indexesToReorganise: List<IndexInfo>
) : IMetricRecommendation()
