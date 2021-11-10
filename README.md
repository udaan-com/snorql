# snor🪀l

# Overview

snorql a.k.a. SQL-Monitoring is an open-source, free-to-use project developed at udaan aimed at diagnosing & resolving common database-related problems using DMV queries.

It comes with two modules

- snorql-framework
- snorql-extension

## What is snorql-framework?

This is a core module that defines the basic structure of the underlying tool. It is extensible in nature and contains all the interfaces and classes that can be implemented according to the requirement.

## What is snorql-extensions?

This is a ready-to-use module built on top of the `snorql-framework` and acts as a repository of SQL metrics that can be consumed in your project layer.

Current extensions include:

- Blocked Queries
- Long-running Queries
- Active Queries
- IndexStats
- ActiveDDL
- UserRole

You can also add your own metrics by following the instructions below under [Build your own metrics](#build-your-own-metrics-using-snorql).

> See issues for details on new extension planned in the roadmap
>

# snorql Usage

### 1. Initialize `SQLCommonMetrics`

Call registerSQLMetrics() during module bootstrap of the application

```kotlin
/
 * RegisterSQLMetrics in App Startup
*/
fun registerSQLMetrics() { 
		// Initialises all the metric provided in snorql-extensions
    SQLCommonMetrics.initialize()
}
```

### 2. Implement Connection Interface

```kotlin
@Singleton
class <ExampleSQLConnection> @Inject constructor(private val jdbi3Factory: Jdbi3Factory) : Connection {
    override fun <T> run(databaseName:String, query: String,mapClass: Class<T>,params: Map<String, *>): List<T> {
        // using a db instance, execute the query, Bind to <T>
    }

    override fun storeData(databaseName:String, tableName: String, columns: List<String>, rows: List<List<Any>>) {
        // using a db instance, persist the data to database
    }
}
```

You can use any SQL database driver. Below is an example using `JDBI` Driver.

```kotlin
class SQlServerConnection constructor(private val jdbi3Factory: Jdbi3Factory) : Connection {
    override fun <T> run(databaseName:String, query: String, mapClass: Class<T>,params: Map<String, *>): List<T> {
        return if(params.isNotEmpty()){
            jdbi3Factory.getInstance(databaseName).withHandle<List<T>, Exception> { handle ->
                handle.createQuery(query).bindMap(params).mapTo(mapClass).toList()
            }
        } else{
            jdbi3Factory.getInstance(databaseName).withHandle<List<T>, Exception> { handle ->
                handle.createQuery(query).mapTo(mapClass).toList() as List<T>
            }
        }
    }

    override fun storeData(databaseName:String, tableName: String,columns: List<String>, rows: List<List<Any>>) {
        val columnStr = columns.joinToString(", ")
        return jdbi3Factory.getInstance(databaseName).withHandle<Unit, Exception> { handle ->
            handle.prepareBatch("INSERT INTO $tableName ($columnStr) VALUES (<values>) ")
                .bindList("values", rows)
                .execute()
        }
    }
}
```

### 3. Generate the metric response

Make a call to `<SqlMetricManager.getMetric()>` with appropriate input to get the result.

Below is an example to consume the metric as an API.

```kotlin
/
 * Fetches list of active-queries metric for a [activeQueryMetricInput]
 */
@POST
@Path("activeQueries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
fun getActiveQueryMetric(
    @RequestBody activeQueryInput: ActiveQueryInput,
    @Suspended asyncResponse: AsyncResponse
) = asyncResponse.with {
    val response = SqlMetricManager.getMetric<ActiveQueryInput, ActiveQueryResult, IMetricRecommendation>(
        SQLMetricTypes.ACTIVE_QUERIES.metricId, metricInput
  }
}
```

## Build your own metrics using snorql

Let's see how we can `add a new metric` inside `snorql-extensions` in just 7 simple steps

1. Add a new json object for your metric to `sql-monitoring-conf.json`

```json
"<metricTypeName_nameOfMetric>": {
    "queries": {
      "main": "<metricQuery>"
   },
    "supportsHistorical": <boolean>,
    "supportsRealTime": <boolean>,
    "isParameterized": <boolean>
  }
```

1. Create a `new enum member` for your Metric in the Enum Class

```kotlin
enum class <MetricCategoryEnums>(private val metricId:String):IMtericId {
    <METRIC_NAME>("<metricName>");
    override fun getId(): String {
       TODO("Implement this")
    }
}
```

1. Create a  `MetricDTOClass`

```kotlin
data class <MetricDTO> (
     TODO("Add members for your metric")
)
```

1. Create new `MetricInputClass`

```kotlin
data class <MetricInput>(
      TODO("Override <MetricInput> members")
			TODO("Add your members")
) : MetricInput()
```

1. Create a `MetricResultClass`

```kotlin
data class <MetricResult>(val queryList: List<MetricDTO>) : IMetricResult()
```

Now that we have created our model classes, we can use them to implement our metric

1. Create a `MetricClass` 

```kotlin
class <Metric>: IMetric<T, R, IMetricRecommendation>{
    override fun getMetricResult(metricInput: MetricInputClass,metricConfig: MetricResultClass): MetricResultClass {
        // Business logic to fetch metric query, execute and format it according to <MetricDTO> 
        TODO("Implement this")
    }
    
    override fun getMetricResponseMetadata(metricInput: MetricInputClass, metricOutput: MetricOutput<MetricResultClass, IMetricRecommendation>): Map<String, Any>? {
        // Business logic to add your metadata (ex: Metric Input Object, Metric Query, etc..)
        TODO("Implement this")
    }

    override fun saveMetricResult(metricInput: MetricInput, result: IMetricResult) {
        // Logic to persist the [result] data object to your data store
        TODO("Implement this")
    }	
}
```

1. Finally, Add your Metric to the `SQLCommonMetric` → `initialize()`

```kotlin
object SQLCommonMetrics {
    fun initialize() {
        SqlMetricManager.addMetric(<MetricCategoryEnums>.<METRIC>.getId(), <MetricEnumName>())
    }
}
```

[Here's an example to guide to through to create your first metric](snorql-extensions/README.md)