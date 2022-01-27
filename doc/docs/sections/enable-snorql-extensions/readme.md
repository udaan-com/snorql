
Pre-requisites: [How to integrate snorql in your project](#how-to-integrate-snorql-in-your-project)

#### Add this library as a dependency:
```xml
    <dependency>
        <groupId>com.udaan.snorql</groupId>
        <artifactId>snorql-extensions</artifactId>
        <version>${versions.snorql-extensions}</version>
    </dependency>
```

#### Add this these `properties` to your parent `pom`:
```xml
    <properties>
        <versions.snorql-extensions>[1.0,2.0)</versions.snorql-extensions>
    </properties>
```

#### Initialize `SQLCommonMetrics`

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

