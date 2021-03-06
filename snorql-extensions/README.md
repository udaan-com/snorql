# Overview
This is built using `snorql-framework` and acts as a repository of SQL metrics that can be consumed in your project layer.
It exposes useful SQL metrics that can be integrated & used with your application

## To add snorql-extensions to your project
[Follow the steps given here](../README.md/#how-to-integrate-snorql-in-your-project)

## Example to build custom metric using snorql-framework

Here we will be taking `activeQueries` in `snorql-extensions` as an example metric to walk through step by step in the process of adding your own metric

1. Added `activeQueries` configuration in the path `snorql-extensions/src/main/resources/sql-monitoring-conf.json`.

```json
{
  "performance_activeQueries": {
    "queries": {
      "main": "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan\n WHERE r.wait_type NOT LIKE 'SP_SERVER_DIAGNOSTICS%' \n ORDER BY r.blocking_session_id"
    },
    "supportsHistorical": true,
    "supportsRealTime": true,
    "isParameterized": false
  }
}
```

Explanation:

`performance_activeQueries` - This is the metric name that will be used later to refer to this metric.

`queries` - This has multiple queries which will be used to build the `<MetricResult>`. 

`supportsHistorical` - Indicates whether the metric data source can come from historical storage.

`supportsRealTime` - Indicates whether the metric data can only be queried from real-time data sources.

`isParameterized` - A truthy value indicates the query is parameterized & vice versa.

---

2. Created the Metric Enum `PerformaceEnums` class & added the `ACTIVE_QUERIES` enum

```kotlin
package com.udaan.snorql.extensions.performance

import com.udaan.snorql.framework.IMtericId

enum class PerformanceEnums(private val metricId:String):IMtericId {

    ACTIVE_QUERIES("activeQueries");

    override fun getId(): String {
        return "performance_" + this.metricId
    }
}
```

NOTE: The metric name returned by the `getId()` method should be similar to the metric name given in the JSON in step 1.

---

3. Added a `ActiveQueryDTO` data class

```kotlin
package com.udaan.snorql.extensions.performance.models

data class ActiveQueryDTO(
    val sessionId: Int,
    val status: String,
    val blockedBy: Int,
    val waitType: String?,
    val waitResource: String?,
    val waitTime: String?,
    val cpuTime: Int?,
    val logicalReads: Int?,
    val reads: Int?,
    val writes: Int?,
    val elapsedTime: String,
    val queryText: String,
    val storedProc: String,
    val command: String,
    val loginName: String,
    val hostName: String,
    val programName: String,
    val hostProcessId: Int,
    val lastRequestEndTime: String,
    val loginTime: String,
    val openTransactionCount: Int
)
```

Explanation:

This `ActiveQueryDTO` corresponds to the result that would be obtained when the query specified against `performance_activeQueries` in the `sql-monitoring-conf.json` is executed.

---

4. Added `ActiveQueryInput` data class

```kotlin
import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.framework.models.IMetricResult
import com.udaan.snorql.framework.models.MetricInput
import com.udaan.snorql.framework.models.MetricPeriod

data class ActiveQueryInput(
    override val metricId: String = PerformanceEnums.ACTIVE_QUERIES.getId(),
    override val metricPeriod: MetricPeriod, override val databaseName: String
) : MetricInput()
```

Explanation:

`ActiveQueryInput` class extends MetricInput & overrides the required parameters with respective values

`metricId` - This equals the metricId specified in Step 2.

`metricPeriod` - Has type MetricPeriod that allows 2 values (REAL_TIME & HISTORICAL).

`databaseName` - The database against which the query will be executed.

Note: If this metric had been parameterized, additional parameters required would have been specified in this `ActiveQueryInput` class.

---

5. Created `ActiveQueryResult` data class

```kotlin
data class ActiveQueryResult(val queryList: List<ActiveQueryDTO>) : IMetricResult()
```

Explanation:

This `ActiveQueryResult` wrapper class encloses the output of the `<Metric>` class.

---

6. Created a  `ActiveQueriesMetric` class

```kotlin
package com.udaan.snorql.extensions.performance.metrics

import com.udaan.snorql.extensions.performance.models.ActiveQueryDTO
import com.udaan.snorql.extensions.performance.models.ActiveQueryInput
import com.udaan.snorql.extensions.performance.models.ActiveQueryResult
import com.udaan.snorql.framework.SQLMonitoringConfigException
import com.udaan.snorql.framework.metric.IMetric
import com.udaan.snorql.framework.metric.SqlMetricManager
import com.udaan.snorql.framework.models.*

class ActiveQueriesMetric :
    IMetric<ActiveQueryInput, ActiveQueryResult, IMetricRecommendation> {

    override fun getMetricResult(
        metricInput: ActiveQueryInput,
        metricConfig: MetricConfig
    ): ActiveQueryResult {
        val query =
            metricConfig.queries["main"]
                ?: throw SQLMonitoringConfigException("SQL config query [main] not found under config [${metricInput.metricId}]")

            val result = SqlMetricManager.queryExecutor.execute<ActiveQueryDTO>(metricInput.databaseName, query)
            return ActiveQueryResult(result)
    }

    override fun getMetricResponseMetadata(
        metricInput: ActiveQueryInput,
        metricOutput: MetricOutput<ActiveQueryResult, IMetricRecommendation>
    ): Map<String, Any>? {
        val responseMetadata = mutableMapOf<String, Any>()
        val query =
            getMetricConfig(metricInput.metricId).queries["main"]
        responseMetadata["underlyingQueries"] = listOf(query)
        return responseMetadata
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {}
}
```

Explanation:

The class `ActiveQueriesMetric` implements `IMetric` with its `ActiveQueryInput` & `ActiveQueryResult` data classes that we defined above.

`getMetricResult()` fetches the query to be executed, & passes it along with the databaseName to the `execute()` which takes care of running the query & returning the result.

`getMetricResponseMetadata()` is responsible for formulating the final response. The response of the same will be something similar to the json given below:

```json
{
   "metricInput":{
      "metricId":"performance_activeQueries",
      "metricPeriod":"REAL_TIME",
      "databaseName":"udprodlogisticssql/db-logisitcs-prod",
      "from":null,
      "to":null,
      "recommendationRequired":false
   },
   "metricOutput":{
      "result":{
         "queryList":[]
      },
      "recommendation":null
   },
   "metadata":{
      "underlyingQueries":[
         "SELECT sessionId=s.session_id\n ,status=r.STATUS\n ,blockedBy=r.blocking_session_id\n ,waitType=r.wait_type\n, waitResource=r.wait_resource\n, waitTime=CONVERT(VARCHAR, DATEADD(ms, r.wait_time, 0), 8)\n, cpuTime=r.cpu_time\n, logicalReads=r.logical_reads\n, r.reads\n ,r.writes\n,elapsedTime=CONVERT(varchar, (r.total_elapsed_time/1000 / 86400))+ 'd ' +\n CONVERT(VARCHAR, DATEADD(ms, r.total_elapsed_time, 0), 8)\n ,queryText=CAST((\n         '<?query --  ' + CHAR(13) + CHAR(13) + Substring(st.TEXT, (r.statement_start_offset / 2) + 1, (\n  (\n CASE r.statement_end_offset\n WHEN - 1\n THEN Datalength(st.TEXT)\n  ELSE r.statement_end_offset\n END - r.statement_start_offset\n) / 2\n) + 1) + CHAR(13) + CHAR(13) + '--?>'\n ) AS XML)\n, storedProc=COALESCE(\n QUOTENAME(DB_NAME(st.dbid)) + N'.' + \n QUOTENAME(OBJECT_SCHEMA_NAME(st.objectid, st.dbid)) + N'.' +\n QUOTENAME(OBJECT_NAME(st.objectid, st.dbid)), ''\n)\n  --,qp.query_plan AS 'xml_plan'  -- uncomment (1) if you want to see plan\n, r.command\n ,loginName=s.login_name\n ,hostName=s.host_name\n ,programName=s.program_name\n ,hostProcessId=s.host_process_id\n ,lastRequestEndTime=s.last_request_end_time\n ,loginTime=s.login_time\n ,openTransactionCount=r.open_transaction_count\n FROM sys.dm_exec_sessions AS s\n INNER JOIN sys.dm_exec_requests AS r ON r.session_id = s.session_id\n CROSS APPLY sys.dm_exec_sql_text(r.sql_handle) AS st\n --OUTER APPLY sys.dm_exec_query_plan(r.plan_handle) AS qp -- uncomment (2) if you want to see plan\n WHERE r.wait_type NOT LIKE 'SP_SERVER_DIAGNOSTICS%' \n ORDER BY r.blocking_session_id"
      ]
   }
}
```

`saveMetricResult()` should have the logic to save the metricResult to a persistent store.(Persistence is not supported yet. Hence, the method definition is empty)

---

7. Registered the metric using `SqlMetricManager.addMetric()`

```kotlin
package com.udaan.snorql.extensions

import com.udaan.snorql.extensions.performance.PerformanceEnums
import com.udaan.snorql.extensions.performance.metrics.ActiveQueriesMetric
import com.udaan.snorql.framework.metric.SqlMetricManager

object SQLCommonMetrics {
    fun initialize() {

        // register performance related metric here
        SqlMetricManager.addMetric(PerformanceEnums.ACTIVE_QUERIES.getId(), ActiveQueriesMetric())
    }
}
```