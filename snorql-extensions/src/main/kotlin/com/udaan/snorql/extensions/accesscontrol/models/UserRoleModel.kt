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

package com.udaan.snorql.extensions.accesscontrol.models

import com.udaan.snorql.extensions.accesscontrol.AccessControlEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod

/**
 * Wrapper class to hold individual user role of UserRoleMetric
 *
 * @property name name of the user role
 * @property role privileges or user group name
 * @property type tpe of role (SQL User/External Group/External User/etc)
 * @constructor Create user role wrapper for each user role
 */
data class UserRoleDTO (
        val name: String,
        val role: String,
        val type: String
)


/**
 * Wrapper class to hold input for UserRoleMetric
 *
 * @property metricId id of UserRoleMetric
 * @property metricPeriod
 * @property databaseName database on which metric is to be used
 * @constructor Create User role input
 */
data class UserRoleInput(
        override val metricId: String = AccessControlEnums.USER_ROLE.getId(),
        override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()

/**
 * Wrapper class to hold the result of UserRoleMetric
 *
 * Data class UserRoleResult holds a list of UserRoleDTO which is returned to the user
 *
 * @property queryList list of UserRoleDTO
 * @constructor Create User role result
 */
data class UserRoleResult(val queryList: List<UserRoleDTO>) : IMetricResult()