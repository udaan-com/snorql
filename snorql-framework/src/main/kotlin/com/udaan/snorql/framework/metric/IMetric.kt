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
 * [IMetric] The interface to be implemented by Metric Class which represents a 'metric'
 *
 * Instances of [IMetric] must have input, result and recommendation model classes defined
 *
 * @param T Metric Input Model Class of type [MetricInput]
 * @param O Metric Result Model class of type [IMetricResult]
 * @param R Recommendation Model class of type [IMetricRecommendation]
 */
interface IMetric<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation> {

    /**
     * Fetch metric configuration from [sql-monitoring-conf.json]
     *
     * @param metricId id of metric
     * @return Metric Configuration wrapped in [MetricConfig]
     */
    fun getMetricConfig(metricId: String): MetricConfig {
        return SqlMetricManager.configuration.get(metricId)
    }

    /**
     * Generates metric output
     *
     * @param metricInput Metric Input wrapped in metric input model class [T]
     * @return Metric Output wrapped in [MetricOutput]
     */
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
     * Persist metric result
     *
     * @param metricInput Metric Input
     * @param result Metric Result wrapped in metric result model class
     */
    fun saveMetricResult(metricInput: MetricInput, result: IMetricResult)


    /**
     * Generates metric response
     *
     * The metric response can be serialized directly.
     *
     * @param metricInput Metric Input
     * @return [MetricResponse] object which contains metric input, metric output and response metadata
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
     * @param metricInput Metric Input
     * @param metricOutput Metric Output
     * @return [Map] of response metadata
     */
    fun getMetricResponseMetadata(metricInput: T, metricOutput: MetricOutput<O, R>): Map<String, Any>? {
        return null
    }

    /**
     * Get metric recommendations
     *
     * @param metricInput Metric Input
     * @param metricResult Metric Output
     * @return Metric recommendations
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
     * @param metricInput Metric Input
     * @param metricConfig Metric Configuration
     * @return Metric Result of type [O]
     */
    abstract fun getMetricResult(
        metricInput: T,
        metricConfig: MetricConfig
    ): O
}