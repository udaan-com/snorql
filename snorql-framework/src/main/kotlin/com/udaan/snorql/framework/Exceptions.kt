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


package com.udaan.snorql.framework

/**
 * Exception class when a metric configuration cannot be found
 *
 * @param message Error message string
 */
class SQLMonitoringConfigException(message: String): Exception(message)

/**
 * Exception class when user supplied connection instance is not found, or unusable
 *
 * @param message Error message string
 */
class SQLMonitoringConnectionException(message: String): Exception(message)

/**
 * Exception class when snorql is unable to give metric response back to the user due to some error
 *
 * @param message Error message string
 */
class SQLMonitoringException(message: String): Exception(message)