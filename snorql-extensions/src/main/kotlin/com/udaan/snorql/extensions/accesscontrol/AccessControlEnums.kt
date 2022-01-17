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

package com.udaan.snorql.extensions.accesscontrol

import com.udaan.snorql.framework.IMtericId

/**
 * Access control metric enums
 *
 * @property metricId id of access control metric in context
 * @constructor Create Access control enums for an access control metric
 */
enum class AccessControlEnums(private val metricId:String):IMtericId {

    /**
     * Map userRole to USER_ROLE
     */
    USER_ROLE("userRole");

    /**
     * Function used to fetch id of this access control metric instance
     */
    override fun getId(): String {
        return "accessControl_" + this.metricId
    }

}