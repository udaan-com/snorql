package com.udaan.snorql.extensions.storage.metrics

import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyDTO
import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyInput
import com.udaan.snorql.extensions.storage.models.DbIndexRedundancyResult
import com.udaan.snorql.extensions.storage.models.RedundantReasonDTO
import com.udaan.snorql.extensions.storage.models.RedundantReasons
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.metric.SqlMetricManager.logger
import com.udaan.snorql.framework.models.IMetricRecommendation
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricConfig
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricOutput

class DbIndexRedundancyMetric :
    IMetric<DbIndexRedundancyInput, DbIndexRedundancyResult, IMetricRecommendation> {
    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        TODO("Not yet implemented")
    }

    override fun getMetricResult(
        metricInput: DbIndexRedundancyInput,
        metricConfig: MetricConfig
    ): DbIndexRedundancyResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException(
                    "SQL config query [main] not found under config " +
                            "[${metricInput.metricId}]"
                )
        val primaryResult =
            SqlMetricManager.queryExecutor.execute<DbIndexRedundancyDTO>(metricInput.databaseName, query)
        val secondaryResult = aggregatedSecondaryResult(metricInput.secondaryDatabaseNames, query)
        val finalResult = if (secondaryResult.isNullOrEmpty()) {
            logger.info("[getMetricResult] Secondary List is null.")
            analyseIndexes(primaryResult)
        } else {
            logger.info(
                "[getMetricResult] Secondary List is not null. Secondary databases: " +
                        metricInput.secondaryDatabaseNames
            )
            logger.debug("Secondary List: $secondaryResult")
            val mergedMetricResult = mergeIdxRedundancyLists(primaryResult, secondaryResult)
            logger.debug("[getMetricResult] Merged Metric Result: $mergedMetricResult")
            analyseIndexes(mergedMetricResult)
        }
        logger.debug("Final Result Length: {}", finalResult.size)
        return DbIndexRedundancyResult(finalResult)
    }

    private fun aggregatedSecondaryResult(
        secondaryDatabaseNames: List<String>?,
        query: String
    ): List<DbIndexRedundancyDTO>? = if (!secondaryDatabaseNames.isNullOrEmpty()) {
        val secondaryResults = mutableListOf<List<DbIndexRedundancyDTO>>()
        secondaryDatabaseNames.forEach { databaseName ->
            secondaryResults.add(
                SqlMetricManager.queryExecutor.execute(databaseName, query)
            )
        }
        var finalList = mutableListOf<DbIndexRedundancyDTO>()
        secondaryResults.forEach { result ->
            finalList = if (finalList.isEmpty()) result.toMutableList()
            else {
                mergeIdxRedundancyLists(finalList, result).toMutableList()
            }
        }
        finalList
    } else {
        null
    }

    private fun analyseIndexes(indexList: List<DbIndexRedundancyDTO>): List<DbIndexRedundancyDTO> {
        val analyzedIndexes = mutableListOf<DbIndexRedundancyDTO>()
        val groupedByTable = indexList.groupBy { it.tableName }
        groupedByTable.keys.forEach { tableName ->
            val analyzedTableIndexes = mutableListOf<DbIndexRedundancyDTO>()
            val indexesToSkip = mutableSetOf<DbIndexRedundancyDTO>()
            val tableIndexes = groupedByTable[tableName]?.filter { it.indexName != null }
                ?.sortedByDescending { it.indexColumnNrs?.length }

            // Step 1: Classifying Unused Indexes
            val unusedIndexes = tableIndexes?.filter { (it.indexUsage ?: 0) < 10 && !it.isUnique }
            unusedIndexes?.forEach { unusedIndex ->
                unusedIndex.reason = RedundantReasonDTO(
                    message = "UNUSED Index", type = RedundantReasons.UNUSED, servingIndex = null
                )
                analyzedTableIndexes.add(unusedIndex)
                indexesToSkip.add(unusedIndex)
            }

            // Step 2: Adding unique indexes to be skipped
            val uniqueIndexes = tableIndexes?.filter { !indexesToSkip.contains(it) && it.isUnique }
            analyzedTableIndexes.addAll(uniqueIndexes ?: listOf())
            indexesToSkip.addAll(uniqueIndexes ?: listOf())
            logger.debug("[analyseIndexes] Unique Indexes: $uniqueIndexes")

            tableIndexes?.forEach { indexInfo ->

                if (indexesToSkip.contains(indexInfo)) return@forEach

                // Add self to analyzed indexes
                analyzedTableIndexes.add(indexInfo)
                indexesToSkip.add(indexInfo)

                // Step 3: Finding Duplicate Indexes
                val duplicateIndexes = tableIndexes.filter {
                    !indexesToSkip.contains(it) && isDuplicate(
                        indexInfo,
                        it
                    )
                }
                duplicateIndexes.forEach { duplicateIndex ->
                    duplicateIndex.reason = RedundantReasonDTO(
                        message = "DUPLICATE of ${indexInfo.indexName}",
                        type = RedundantReasons.DUPLICATE,
                        servingIndex = indexInfo.indexName
                    )
                    analyzedTableIndexes.add(duplicateIndex)
                }
                indexesToSkip.addAll(duplicateIndexes)
                logger.debug("[analyseIndexes] Duplicate Indexes: $duplicateIndexes")
                logger.debug("[analyseIndexes] Classified Indexes: $analyzedIndexes")

                // Step 4: Finding overlapping indexes
                val overlappingIndexes = tableIndexes.filter {
                    !indexesToSkip.contains(it) && isOverlapping(
                        indexInfo,
                        it
                    ) && isOverlapping(indexInfo, it)
                }
                overlappingIndexes.forEach { overlappingIndex ->
                    overlappingIndex.reason = RedundantReasonDTO(
                        message = "${indexInfo.indexName} OVERLAPS this index",
                        type = RedundantReasons.OVERLAPPING, servingIndex = indexInfo.indexName
                    )
                    analyzedTableIndexes.add(overlappingIndex)
                }
                indexesToSkip.addAll(overlappingIndexes)
                logger.debug("[analyseIndexes] Overlapping Indexes: $overlappingIndexes")
                logger.debug("[analyseIndexes] Classified Indexes: $analyzedIndexes")

                // Step 5: Finding Similar Indexes
                val similarIndexes = tableIndexes.filter {
                    !indexesToSkip.contains(it) && isSimilar(indexInfo, it)
                }
                similarIndexes.forEach { similarIndex ->
                    similarIndex.reason = RedundantReasonDTO(
                        type = RedundantReasons.SIMILAR,
                        servingIndex = indexInfo.indexName,
                        message = "Index is SIMILAR to ${indexInfo.indexName}. Include missing Include columns in " +
                                "${indexInfo.indexName}, post which this index can be deleted."
                    )
                    analyzedTableIndexes.add(similarIndex)
                }
                indexesToSkip.addAll(similarIndexes)
                logger.debug("[analyseIndexes] Similar Indexes: $similarIndexes")
                logger.debug("[analyseIndexes] Classified Indexes: $analyzedIndexes")
            }
            analyzedIndexes.addAll(analyzedTableIndexes)
        }
        return analyzedIndexes
    }

    private fun isDuplicate(parentIndex: DbIndexRedundancyDTO, childIndex: DbIndexRedundancyDTO): Boolean {
        val parentIncludeCols = parentIndex.includeColumnNrs?.split(" ")?.toSet()
        val childIncludeCols = childIndex.includeColumnNrs?.split(" ")?.toSet()
        logger.debug(
            "[DbIndexRedundancyMetric][isDuplicate] Parent Include Cols: $parentIncludeCols\n" +
                    "Child Include Cols: $childIncludeCols"
        )
        if (parentIndex.indexColumnNrs == childIndex.indexColumnNrs && parentIncludeCols == childIncludeCols) {
            return true
        }
        return false
    }

    private fun isOverlapping(parentIndex: DbIndexRedundancyDTO, childIndex: DbIndexRedundancyDTO): Boolean {
        val parentIncludeCols =
            parentIndex.includeColumnNrs?.split(" ")?.filterNot { it.isNullOrBlank() }?.toSet() ?: setOf()
        val childIncludeCols =
            childIndex.includeColumnNrs?.split(" ")?.filterNot { it.isNullOrBlank() }?.toSet() ?: setOf()
        logger.debug(
            "[DbIndexRedundancyMetric][isDuplicate] Parent Include Cols: $parentIncludeCols\n" +
                    "Child Include Cols: $childIncludeCols"
        )
        if (checkStartsWithWord(
                parentIndex.indexColumnNrs,
                childIndex.indexColumnNrs
            ) // && (parentIncludeCols subtract childIncludeCols).isEmpty()
            && (childIncludeCols subtract parentIncludeCols).isEmpty()
        ) {
            return true
        }
        return false
    }

    private fun isSimilar(parentIndex: DbIndexRedundancyDTO, childIndex: DbIndexRedundancyDTO): Boolean {
        if (parentIndex.indexColumnNrs == childIndex.indexColumnNrs
            && parentIndex.includeColumnNrs != childIndex.includeColumnNrs
        ) {
            return true
        }
        return false
    }

    override fun getMetricResponseMetadata(
        metricInput: DbIndexRedundancyInput,
        metricOutput: MetricOutput<DbIndexRedundancyResult, IMetricRecommendation>
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

    private fun checkStartsWithWord(longerString: String?, shorterString: String?): Boolean {
        if (longerString == null) return false
        if (shorterString == null) return true
        val longerArr = longerString.split(" ")
        val shortArr = shorterString.split(" ")
        if (longerArr.size < shortArr.size) return false
        shortArr.forEachIndexed { index, element ->
            if (element != longerArr[index]) {
                return false
            }
        }
        return true
    }

    private fun mergeIdxRedundancyLists(
        primaryList: List<DbIndexRedundancyDTO>,
        secondaryList: List<DbIndexRedundancyDTO>
    ): List<DbIndexRedundancyDTO> {
        if (secondaryList.isEmpty()) return primaryList
        logger.debug("[mergeIdxRedundancyLists] Secondary Index List: $secondaryList")
        val finalList = mutableListOf<DbIndexRedundancyDTO>()
        primaryList.forEachIndexed() { idx, primaryResult ->
            val idxInfo = secondaryList.find {
                it.tableName == primaryResult.tableName && it.indexName == primaryResult.indexName
            }
            logger.debug("[mergeIdxRedundancyLists] Primary Result: $primaryResult")
            logger.debug("[mergeIdxRedundancyLists] Secondary Rslt: $idxInfo")
            finalList.add(
                idx,
                DbIndexRedundancyDTO(
                    tableObjectId = primaryResult.tableObjectId,
                    tableName = primaryResult.tableName,
                    indexId = primaryResult.indexId,
                    indexName = primaryResult.indexName,
                    indexType = primaryResult.indexType,
                    indexUsage = (primaryResult.indexUsage ?: 0).plus(idxInfo?.indexUsage ?: 0),
                    indexUpdates = (primaryResult.indexUpdates ?: 0).plus(idxInfo?.indexUpdates ?: 0),
                    indexColumnNrs = primaryResult.indexColumnNrs,
                    indexColumnNames = primaryResult.indexColumnNames,
                    includeColumnNrs = primaryResult.includeColumnNrs,
                    includeColumnNames = primaryResult.includeColumnNames,
                    indexSizeInKb = primaryResult.indexSizeInKb,
                    isUnique = primaryResult.isUnique
                )
            )
            logger.debug("[mergeIdxRedundancyLists] Final Rslt: ${finalList[idx]}")
        }
        return finalList
    }
}
