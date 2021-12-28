package com.udaan.snorql.framework.models

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.SqlMetricManager
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.lang.Exception
import kotlin.test.fail

class TestConfiguration {

    private val CONFIG_FILE_LOCATION = "/sql-monitoring-conf.json"

    private val objectMapper: ObjectMapper
        get() {
            return jacksonObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).registerKotlinModule()
        }

//    private val configuration: Configuration by lazy {
//        val file = SqlMetricManager::class.java.getResource(CONFIG_FILE_LOCATION).readText()
//        if (file.isNotEmpty()) {
//            val typeRef = object : TypeReference<Map<String, MetricConfig>>() {}
//            Configuration(objectMapper.readValue(file, typeRef))
//        } else {
//            SqlMetricManager.logger.warn("SQL monitoring configuration file [${CONFIG_FILE_LOCATION}] not found in classpath")
//            Configuration(mapOf())
//        }
//    }

    private val activeQueriesMetricId = "performance_activeQueries"
    private val longRunningQueriesMetricId = "performance_longRunningQueries"
    private val userRoleMetricId = "accessControl_userRole"
    private val storageDbMetricId = "storage_db"
    private val storageTableMetricId = "storage_table"
    private val tableUnusedIndexMetricId = "storage_tableUnusedIndex"


    private val activeQueriesMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf(
            "main" to "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes,\n r.percent_complete as percentComplete\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan \n ORDER BY r.blocking_session_id"
        ),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    private val longRunningQueriesMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf(
            "main" to "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan\n WHERE r.wait_type NOT LIKE 'SP_SERVER_DIAGNOSTICS%'  AND r.total_elapsed_time > :elapsedTimeParam \n AND r.session_id != @@SPID ORDER BY r.blocking_session_id"
        ),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = true,
        referenceDoc = "",
        description = ""
    )

    private val userRolesMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf("main" to "select dp1.name, string_agg(dp2.name,', ') as role, dp1.type as type from sys.database_role_members drm\njoin sys.database_principals dp1 on dp1.principal_id=drm.member_principal_id\njoin sys.database_principals dp2 on dp2.principal_id=drm.role_principal_id\ngroup by dp1.name, dp1.type"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    private val storageDbMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf(
            "main" to "sp_spaceused  @oneresultset = 1",
            "dbSize" to "SELECT CONVERT(double precision,DATABASEPROPERTYEX( :databaseName, 'MaxSizeInBytes'))/(1024*1024*1024) AS DatabaseDataMaxSizeInBytes"
        ),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-spaceused-transact-sql?view=sql-server-ver15#examples",
        description = "Displaying updated space information about a database"
    )

    private val storageTableMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf("main" to "sp_spaceused @oneresultset = 1, @objname = :tableName;"),
        supportsHistorical = true,
        supportsRealTime = true,
        isParameterized = true,
        referenceDoc = "https://docs.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-spaceused-transact-sql?view=sql-server-ver15#examples",
        description = "Displaying disk space information about a table"
    )

    private val tableUnusedIndexMetricConfig: MetricConfig = MetricConfig(
        queries = mapOf("main" to "SELECT OBJECT_NAME(S.[OBJECT_ID]) AS OBJECT_NAME,\n       I.[NAME]                   AS INDEX_NAME,\n       USER_SEEKS,\n       USER_SCANS,\n       USER_LOOKUPS,\n       USER_UPDATES,\n       string_agg(c.name,', ') as columnName\nFROM SYS.DM_DB_INDEX_USAGE_STATS AS S\n         INNER JOIN SYS.INDEXES AS I ON I.[OBJECT_ID] = S.[OBJECT_ID] AND I.INDEX_ID = S.INDEX_ID\ninner join sys.index_columns ic on ic.index_id = i.index_id and ic.object_id=i.object_id\njoin sys.columns c on c.column_id=ic.column_id and c.object_id=i.object_id\nWHERE OBJECTPROPERTY(S.[OBJECT_ID], 'IsUserTable') = 1\n  and i.Name is not null\n  and OBJECT_NAME(S.[OBJECT_ID]) = :tableName\ngroup by OBJECT_NAME(S.[OBJECT_ID]),I.[NAME],USER_SEEKS,  USER_SCANS,USER_LOOKUPS,USER_UPDATES,ic.index_id;\n"),
        supportsHistorical = false,
        supportsRealTime = true,
        isParameterized = false,
        referenceDoc = "",
        description = ""
    )

    private val configMap: Map<String, MetricConfig> = mapOf(
        activeQueriesMetricId to activeQueriesMetricConfig,
        longRunningQueriesMetricId to longRunningQueriesMetricConfig,
        userRoleMetricId to userRolesMetricConfig,
        storageDbMetricId to storageDbMetricConfig,
        storageTableMetricId to storageTableMetricConfig,
        tableUnusedIndexMetricId to tableUnusedIndexMetricConfig
    )

    private val configuration: Configuration = Configuration(configMap)

    @Test
    fun testGetMetricConfiguration() {

        val incorrectMetricIdList = listOf<String>("", "incorrectId")

        assertEquals(activeQueriesMetricConfig, configuration.get(activeQueriesMetricId))
        assertEquals(longRunningQueriesMetricConfig, configuration.get(longRunningQueriesMetricId))
        assertEquals(userRolesMetricConfig, configuration.get(userRoleMetricId))
        assertEquals(storageDbMetricConfig, configuration.get(storageDbMetricId))
        assertEquals(storageTableMetricConfig, configuration.get(storageTableMetricId))
        assertEquals(tableUnusedIndexMetricConfig, configuration.get(tableUnusedIndexMetricId))

        for (metricId in incorrectMetricIdList) {
            try {
                configuration.get(metricId)
            } catch (e: SQLMonitoringConfigException) {
                continue
            } catch (e: Exception) {
                fail(message = "Configuration.get() failed for metric id: $metricId")
            }
        }
    }
}