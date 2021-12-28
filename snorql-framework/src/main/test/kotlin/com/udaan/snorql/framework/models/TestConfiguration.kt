package com.udaan.snorql.framework.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.udaan.snorql.framework.metric.SqlMetricManager
import org.junit.Test

class TestConfiguration {

    private val CONFIG_FILE_LOCATION = "/sql-monitoring-conf.json"

    private val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false).registerKotlinModule()
        }

    val configuration: Configuration by lazy {
        val file = SqlMetricManager::class.java.getResource(CONFIG_FILE_LOCATION).readText()
        if (file.isNotEmpty()) {
            val typeRef = object : TypeReference<Map<String, MetricConfig>>() {}
            Configuration(objectMapper.readValue(file, typeRef))
        } else {
            SqlMetricManager.logger.warn("SQL monitoring configuration file [${CONFIG_FILE_LOCATION}] not found in classpath")
            Configuration(mapOf())
        }
    }

    private val activeQueriesMetricId = "performance_activeQueries"
    private val longRunningQueriesMetricId = "performance_longRunningQueries"
    private val userRoleMetricId = "accessControl_userRole"
    private val storageDbMetricId = "storage_db"
    private val storageTableMetricId = "storage_table"
    private val tableUnusedIndexMetricId = "storage_tableUnusedIndex"


    private val activeQueriesMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf("main" to "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes,\n r.percent_complete as percentComplete\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan \n ORDER BY r.blocking_session_id"),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    private val longRunningQueriesMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf("main" to "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan\n WHERE r.wait_type NOT LIKE 'SP_SERVER_DIAGNOSTICS%'  AND r.total_elapsed_time > :elapsedTimeParam \n AND r.session_id != @@SPID ORDER BY r.blocking_session_id"),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = true,
        referenceDoc = "",
        description = ""
    )

    @Test
    fun testGetMetricConfiguration() {

    }
}