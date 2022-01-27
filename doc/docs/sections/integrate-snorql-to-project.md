
#### Add this library as a dependency:
```xml
    <dependency>
        <groupId>com.udaan.snorql</groupId>
        <artifactId>snorql-framework</artifactId>
        <version>${versions.snorql-framework}</version>
    </dependency>
```

#### Add this these `properties` to your parent `pom`:
```xml
    <properties>
        <versions.snorql-framework>[1.0,2.0)</versions.snorql-framework>
    </properties>
```

#### Implement Connection Interface

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

#### Generate the metric response

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

