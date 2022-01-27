
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




