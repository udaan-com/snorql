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

package com.udaan.snorql.framework.metric

import com.nhaarman.mockitokotlin2.*
import io.mockk.mockkObject
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class QueryExecutorTest {

    private val demoDTO1 = DemoDTO(
        demoField1 = "RandomString1",
        demoField2 = 1
    )
    private val demoDTO2 = DemoDTO(
        demoField1 = "RandomString2",
        demoField2 = 2
    )

    @Before
    fun beforeTest() {
        mockkObject(SqlMetricManager)
    }

    @After
    fun afterTest() {
        unmockkAll()
    }

    @Test
    fun testExecute() {
        val mockConnection: Connection = mock()
        whenever(mockConnection.run<DemoDTO>(any(), any(), any(), any())).thenAnswer {
            listOf<DemoDTO>(demoDTO1, demoDTO2)
        }

        SqlMetricManager.setConnection(mockConnection)
        assertEquals(
            listOf<DemoDTO>(demoDTO1, demoDTO2),
            SqlMetricManager.queryExecutor.execute("randomDatabaseName", "randomQuery")
        )
        assertEquals(
            listOf<DemoDTO>(demoDTO1, demoDTO2),
            SqlMetricManager.queryExecutor.execute(
                "randomDatabaseName", "randomQuery", mapOf<String, Any>(
                    "param1" to "Param 1 Value"
                )
            )
        )
        assertEquals(
            listOf<DemoDTO>(demoDTO1, demoDTO2),
            SqlMetricManager.queryExecutor.execute(
                "", "", mapOf<String, Any>(
                    "param1" to "Param 1 Value"
                )
            )
        )
        reset(SqlMetricManager.queryExecutor.connection)
        reset(mockConnection)
    }

    @Test
    fun testPersistData() {
        val mockConnection: Connection = mock()
        val randomColumnsList: List<String> = listOf("Column_1", "Column_2", "Column_3")
        val randomRows: List<List<Any>> = listOf(
            listOf("DataCell1", "DataCell2", "DataCell3"),
            listOf("DataCell4", "DataCell5", "DataCell6")
        )

        SqlMetricManager.setConnection(mockConnection)

        assertEquals(
            Unit, SqlMetricManager.queryExecutor.persistData(
                databaseName = "randomDatabaseName",
                tableName = "randomTableName",
                columns = randomColumnsList,
                rows = randomRows
            )
        )

        verify(mockConnection, times(1)).storeData(
            databaseName = "randomDatabaseName",
            tableName = "randomTableName",
            columns = randomColumnsList,
            rows = randomRows
        )

        reset(SqlMetricManager.queryExecutor.connection)
        reset(mockConnection)
    }

}