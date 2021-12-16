/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework.metric

import com.udaan.snorql.framework.models.*

/**
 * I metric
 *
 * @param T
 * @param O
 * @param R
 * @constructor Create empty I metric
 */
interface IMetric<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> {

    /**
     * Get metric config
     *
     * @param metricId
     * @return
     */
    fun getMetricConfig(metricId: String): MetricConfig {
        return SqlMetricManager.configuration.get(metricId)
    }

    private fun getMetricOutput(metricInput: T): MetricOutput<O, R> {
        val metricResult = getMetricResult(metricInput, getMetricConfig(metricInput.metricId))
        val metricRecommendation = if (metricInput.recommendationRequired) {
            getMetricRecommendations(metricInput, metricResult)
        } else {
            null
        }
        return MetricOutput(metricResult, metricRecommendation)
    }

    /**
     * Save metric result
     *
     * @param metricInput
     * @param result
     */
    fun saveMetricResult(metricInput: MetricInput, result: IMetricResult)


    /**
     * Get metric response
     *
     * @param metricInput
     * @return
     */
    fun getMetricResponse(metricInput: T): MetricResponse<O, R> {
        val metricOutput = getMetricOutput(metricInput)
       return MetricResponse<O, R>(
            metricInput,
            metricOutput,
            getMetricResponseMetadata(metricInput, metricOutput)
        )
    }

    /**
     * Get metric response metadata
     *
     * @param metricInput
     * @param metricOutput
     * @return
     */
    fun getMetricResponseMetadata(metricInput: T, metricOutput: MetricOutput<O, R>): Map<String, Any>? {
        return null
    }

    /**
     * Get metric recommendations
     *
     * @param metricInput
     * @param metricResult
     * @return
     */
    fun getMetricRecommendations(
        metricInput: T,
        metricResult: O
    ): R? {
        return null
    }

    /**
     * Get metric result
     *
     * @param metricInput
     * @param metricConfig
     * @return
     */
    abstract fun getMetricResult(
        metricInput: T,
        metricConfig: MetricConfig
    ): O
}