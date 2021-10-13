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

package com.udaan.snorql.extensions

import com.udaan.snorql.extensions.accesscontrol.AccessControlEnums
import com.udaan.snorql.extensions.accesscontrol.metrics.UserRoleMetric
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.metrics.ActiveQueriesMetric
import com.udaan.snorql.extensions.performance.metrics.LongRunningQueriesMetric
import com.udaan.snorql.framework.metric.SqlMetricManager

object SQLCommonMetrics {
    fun initialize() {
        SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_QUERIES.getEnum(), ActiveQueriesMetric())
        SqlMetricManager.addMetric(PerformanceEnums.LONG_RUNNING_QUERIES.getEnum(), LongRunningQueriesMetric())
        SqlMetricManager.addMetric(AccessControlEnums.USER_ROLE.getEnum(), UserRoleMetric())
    }
}