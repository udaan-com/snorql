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

package com.udaan.snorql.framework.models

import com.udaan.snorql.framework.SQLMonitoringConfigException

/**
 * Class responsible for fetching configuration of a metric
 * The configuration of a metric is stored in sql-monitoring-conf.json
 *
 * @property configMap metric ConfigMap instance
 * @constructor Create an instance of Configuration Class for a metric
 */
class Configuration(private val configMap: Map<String, MetricConfig>) {
    /**
     * Get configuration for a [metricId]
     *
     * @param metricId metricId of the metric
     * @return Metric Configuration belonging to [metricId]
     * @throws SQLMonitoringConfigException when configuration is not present against [metricId]
     */
    fun get(metricId: String): MetricConfig = configMap[metricId]
            ?: throw SQLMonitoringConfigException("Config against metric id $metricId not found")

}