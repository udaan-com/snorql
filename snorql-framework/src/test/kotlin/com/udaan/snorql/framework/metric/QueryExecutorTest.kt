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

    // reified test case possible?

    // Udaan-commons

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