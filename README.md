<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/udaan-com/snorql.svg?style=for-the-badge
[contributors-url]: https://github.com/udaan-com/snorql/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/udaan-com/snorql.svg?style=for-the-badge
[forks-url]: https://github.com/udaan-com/snorql/network/members
[stars-shield]: https://img.shields.io/github/stars/udaan-com/snorql.svg?style=for-the-badge
[stars-url]: https://github.com/udaan-com/snorql/stargazers
[issues-shield]: https://img.shields.io/github/issues/udaan-com/snorql.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/snorql/issues
[license-shield]: https://img.shields.io/github/license/udaan-com/snorql.svg?style=for-the-badge
[license-url]: https://github.com/udaan-com/snorql/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://in.linkedin.com/company/udaan
[product-screenshot]: images/screenshot.png

<div id="top"></div>

<!-- PROJECT LOGO -->
<br />
<div align="center">

  <h3 align="center">
    <a href="https://github.com/udaan-com/snorql">SNOR🪀L</a>
  </h3>

  <p align="center">
    snorql a.k.a. SQL-Monitoring is an open-source, free-to-use project developed at udaan aimed at diagnosing & resolving common database-related problems using SQL metrics.
    <br />
    <a href="https://udaan-com.github.io/snorql/sections/about/ghp/"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/udaan-com/snorql">View Demo</a>
    ·
    <a href="https://github.com/udaan-com/snorql/issues">Report Bug</a>
    ·
    <a href="https://github.com/udaan-com/snorql/issues">Request Feature</a>
  </p>
</div>


<!-- TABLE OF CONTENTS -->
## Table of Contents
<ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#usage">Usage</a>
      <ul>
        <li><a href="#how-to-integrate-snorql-in-your-project">How to integrate snorql in your project</a></li>
        <li><a href="#enable-snorql-extensions-in-your-project-optional">Enable snorql-extensions in your project</a></li>
        <li><a href="#build-your-own-custom-metrics-using-snorql">Build your own custom metrics using snorql</a></li>
      </ul>
    </li>
    <li>
      <a href="#kotlin-documentation">Kotlin Documentation</a>
      <ul>
        <li><a href="#snorql-framework-kdocs">snorql-framework KDocs</a></li>
        <li><a href="#snorql-extensions-kdocs">snorql-extension KDocs</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
</ol>

<!-- ABOUT THE PROJECT -->
## About The Project

SNORQL comes with two modules

- snorql-framework
- snorql-extensions

### What is snorql-framework?

This is a basic framework of the underlying tool used to build metrics. It is extensible in nature and contains all the models, interfaces and classes that can be used to build your own metric.


### What is snorql-extensions?

This is built using `snorql-framework` and acts as a repository of SQL metrics that can be consumed in your project layer.
It exposes useful SQL metrics that can be integrated & used with your application

Current extensions include:

- Blocked Queries
- Long-running Queries
- Active Queries <br>
  ... [to view the complete list please click here](https://github.com/udaan-com/snorql/wiki/snorql-extensions)

You can also add your own metrics by following the instructions below under [Build your own custom metrics using snorql](#build-your-own-custom-metrics-using-snorql).

> See issues with [new-metric](https://github.com/udaan-com/snorql/labels/new-metric) label for details on newer extension planned in the roadmap.


<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

* [Kotlin](https://kotlinlang.org/)
* [Maven](https://maven.apache.org/)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage
### How to integrate snorql in your project

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

### Enable 'snorql-extensions' in your project (optional)

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

## Build your own custom metrics using snorql

Let's see how we can build your own custom metric using `snorql-framework` in just 8 simple steps

Step1. Add a new json object for your metric to `sql-monitoring-conf.json`

```json
"<metricName>": {
    "queries": {
      "main": "<metricQuery>"
    },
    "supportsHistorical": <boolean>,
    "supportsRealTime": <boolean>,
    "isParameterized": <boolean>
  }
```

Step2. Create a `new enum member` for your Metric in the Enum Class

```kotlin
enum class <MetricEnum>(private val metricId:String):IMtericId {
    <METRIC_NAME>("<metricName>");
    override fun getId(): String {
       TODO("Implement this")
    }
}
```

Step3. Create a  `MetricDTOClass`

```kotlin
data class <MetricDTO> (
     TODO("Add members for your metric")
)
```

Step4. Create new `MetricInputClass`

```kotlin
data class <MetricInput>(
      TODO("Override <MetricInput> members")
      TODO("Add your members")
) : MetricInput()
```

Step5. Create a `MetricResultClass`

```kotlin
data class <MetricResult>(val queryList: List<MetricDTO>) : IMetricResult()
```

Step6. Create a `MetricRecommendationClass`(optional: Only if your metric supports recommendation)

```kotlin
data class <MetricRecommendation>(val queryList: List<MetricDTO>) : IMetricRecommendation()
```

Now that we have created our model classes, we can use them to implement our metric

Step7. Create a `MetricClass`

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

Step8. Finally, Register your Metric to the `SqlMetricManager`

```kotlin
SqlMetricManager
    .addMetric(<MetricCategoryEnums>.<METRIC>.getId(), <MetricEnumName>())
```
Note: Add the above code(Step8) during the start of your application.

[Here's an example to create your first custom metric](https://github.com/udaan-com/snorql/blob/main/snorql-extensions/README.md)

<p align="right">(<a href="#top">back to top</a>)</p>




<!-- KOTLIN DOCUMENTATION -->
## Kotlin Documentation

### snorql-framework KDocs
Please find the detailed KDoc for snorql-framework [here](https://udaan-com.github.io/snorql/snorql-framework/snorql-framework/).

### snorql-extensions KDocs
Please find the detailed KDoc for snorql-extensions [here](https://udaan-com.github.io/snorql/snorql-extensions/snorql-extensions/).

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- ROADMAP -->
## Roadmap

- [] Feature 1
- [] Feature 2
- [] Feature 3
    - [] Nested Feature

See the [open issues](https://github.com/udaan-com/snorql/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the Apache License. See `LICENSE` for more information.

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

udaan.com - [@twitter_handle](https://twitter.com/twitter_handle) - email@email_client.com

Project Link: [https://github.com/udaan-com/snorql](https://github.com/udaan-com/snorql)

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
<!--
## Acknowledgments

* []()
* []()
* []()
-->

<p align="right">(<a href="#top">back to top</a>)</p>

