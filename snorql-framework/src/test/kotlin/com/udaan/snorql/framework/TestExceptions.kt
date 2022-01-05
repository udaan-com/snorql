/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework

import org.junit.Test
import java.lang.Exception
import kotlin.test.fail
import org.junit.Assert.assertThat
import org.hamcrest.core.Is.`is`

class TestExceptions {

    @Test
    fun testSQLMonitoringConfigException() {
        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringConfigException",
                messageString = "This is a demo exception with message"
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringConfigException) {
            assertThat(e.message, `is`("This is a demo exception with message"))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }

        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringConfigException",
                messageString = ""
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringConfigException) {
            assertThat(e.message, `is`(""))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }
    }

    @Test
    fun testSQLMonitoringConnectionException() {
        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringConnectionException",
                messageString = "This is a demo exception with message"
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringConnectionException) {
            assertThat(e.message, `is`("This is a demo exception with message"))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }

        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringConnectionException",
                messageString = ""
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringConnectionException) {
            assertThat(e.message, `is`(""))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }
    }

    @Test
    fun testSQLMonitoringException() {
        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringException",
                messageString = "This is a demo exception with message"
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringException) {
            assertThat(e.message, `is`("This is a demo exception with message"))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }

        try {
            exceptionThrowingFunction(
                exceptionName = "SQLMonitoringException",
                messageString = ""
            )
            fail("Exception not thrown")
        } catch (e: SQLMonitoringException) {
            assertThat(e.message, `is`(""))
        } catch (e: Exception) {
            fail("Unexpected exception thrown: $e")
        }
    }

    private fun exceptionThrowingFunction(exceptionName: String, messageString: String) {
        when {
            (exceptionName == "SQLMonitoringConfigException") -> {
                throw SQLMonitoringConfigException(messageString)
            }
            (exceptionName == "SQLMonitoringConnectionException") -> {
                throw SQLMonitoringConnectionException(messageString)
            }
            (exceptionName == "SQLMonitoringException") -> {
                throw SQLMonitoringException(messageString)
            }
            else -> {
                throw Exception("Exception $exceptionName does not exist")
            }
        }
    }
}