package com.udaan.snorql.extensions.storage.models

import com.udaan.snorql.extensions.storage.StorageEnums
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod
import org.jdbi.v3.core.mapper.reflect.ColumnName

data class DbIndexRedundancyDTO(
    @ColumnName("TableObjectId")
    val tableObjectId: Int,
    @ColumnName("TableName")
    val tableName: String,
    @ColumnName("IndexId")
    val indexId: Int,
    @ColumnName("IndexName")
    val indexName: String?,
    @ColumnName("IndexType")
    val indexType: String,
    @ColumnName("IndexUsage")
    val indexUsage: Int?,
    @ColumnName("IndexUpdates")
    val indexUpdates: Int?,
    @ColumnName("IndexColumnNrs")
    val indexColumnNrs: String?,
    @ColumnName("IndexColumnNames")
    val indexColumnNames: String?,
    @ColumnName("IncludeColumnNrs")
    val includeColumnNrs: String?,
    @ColumnName("IncludeColumnNames")
    val includeColumnNames: String?,
    @ColumnName("IndexSizeKb")
    val indexSizeInKb: Long,
    @ColumnName("IsUniqueIndex")
    val isUnique: Boolean,
    var reason: RedundantReasonDTO? = null
)

data class DbIndexRedundancyInput(
    override val metricId: String = StorageEnums.DB_INDEX_REDUNDANCY.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val secondaryDatabaseNames: List<String> = listOf()
) : MetricInput()

data class DbIndexRedundancyResult(
    val queryList: List<DbIndexRedundancyDTO>
) : IMetricResult()

data class RedundantReasonDTO(
    val message: String,
    val type: RedundantReasons,
    val servingIndex: String?
)

enum class RedundantReasons {
    DUPLICATE, OVERLAPPING, UNUSED, SIMILAR
}