package com.udaan.snorql.extensions.performance.models

import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.framework.annotations.SnorqlColumnName
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod

/**
 * Model class to hold individual row for the Execution query plan
 * Model class to hold the query result for [QueryPlanStatsMetric]
 * @property stmtText statement text for the step of query analysed
 * @property stmtId Id of the statement analysed
 * @property nodeId Id of the row
 * @property parent id of the parent row
 * @property physicalOp physical operation performed in the sql db
 * @property logicalOp logical operation performed in the sql db
 * @property argument
 * @property definedValues
 * @property estimateRows number of estimated rows returned after analysing this step
 * @property estimateIO estimated I/O for this step
 * @property estimateCPU estimated CPU for this step
 * @property avgRowSize estimated average row size for this step
 * @property totalSubtreeCost estimated cost for this subtree
 * @property outputList
 * @property warnings
 * @property type
 * @property parallel if this step was executed in parallel
 * @property estimateExecutions how many times this step was performed
 */
data class QueryPlanStatsDTO(
    @SnorqlColumnName("StmtText")
    val stmtText: String,
    @SnorqlColumnName("StmtId")
    val stmtId: Int,
    @SnorqlColumnName("NodeId")
    val nodeId: Int,
    @SnorqlColumnName("Parent")
    val parent: Int,
    @SnorqlColumnName("PhysicalOp")
    val physicalOp: String?,
    @SnorqlColumnName("LogicalOp")
    val logicalOp: String?,
    @SnorqlColumnName("Argument")
    val argument: String?,
    @SnorqlColumnName("DefinedValues")
    val definedValues: String?,
    @SnorqlColumnName("EstimateRows")
    val estimateRows: Double,
    @SnorqlColumnName("EstimateIO")
    val estimateIO: Double?,
    @SnorqlColumnName("EstimateCPU")
    val estimateCPU: Double?,
    @SnorqlColumnName("AvgRowSize")
    val avgRowSize: Int?,
    @SnorqlColumnName("TotalSubtreeCost")
    val totalSubtreeCost: Double,
    @SnorqlColumnName("OutputList")
    val outputList: Any,
    @SnorqlColumnName("Warnings")
    val warnings: Any?,
    @SnorqlColumnName("Type")
    val type: String,
    @SnorqlColumnName("Parallel")
    val parallel: Boolean,
    @SnorqlColumnName("EstimateExecutions")
    val estimateExecutions: Int?
)

data class QueryPlanStatsInput(
    override val metricId: String = PerformanceEnums.QUERY_PLAN_STATS.getId(),
    override val metricPeriod: MetricPeriod,
    override val databaseName: String,
    val query: String
): MetricInput()

data class QueryPlanStatsResult(val queryList: List<QueryPlanStatsDTO>) : IMetricResult()
