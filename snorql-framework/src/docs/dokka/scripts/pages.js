var pages = [{"name":"interface IMtericId","description":"com.udaan.snorql.framework.IMtericId","location":"snorql-framework/com.udaan.snorql.framework/-i-mteric-id/index.html","searchKeys":["IMtericId","interface IMtericId"]},{"name":"abstract fun getId(): String","description":"com.udaan.snorql.framework.IMtericId.getId","location":"snorql-framework/com.udaan.snorql.framework/-i-mteric-id/get-id.html","searchKeys":["getId","abstract fun getId(): String"]},{"name":"class SQLMonitoringConfigException(message: String) : Exception","description":"com.udaan.snorql.framework.SQLMonitoringConfigException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-config-exception/index.html","searchKeys":["SQLMonitoringConfigException","class SQLMonitoringConfigException(message: String) : Exception"]},{"name":"fun SQLMonitoringConfigException(message: String)","description":"com.udaan.snorql.framework.SQLMonitoringConfigException.SQLMonitoringConfigException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-config-exception/-s-q-l-monitoring-config-exception.html","searchKeys":["SQLMonitoringConfigException","fun SQLMonitoringConfigException(message: String)"]},{"name":"class SQLMonitoringConnectionException(message: String) : Exception","description":"com.udaan.snorql.framework.SQLMonitoringConnectionException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-connection-exception/index.html","searchKeys":["SQLMonitoringConnectionException","class SQLMonitoringConnectionException(message: String) : Exception"]},{"name":"fun SQLMonitoringConnectionException(message: String)","description":"com.udaan.snorql.framework.SQLMonitoringConnectionException.SQLMonitoringConnectionException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-connection-exception/-s-q-l-monitoring-connection-exception.html","searchKeys":["SQLMonitoringConnectionException","fun SQLMonitoringConnectionException(message: String)"]},{"name":"class SQLMonitoringException(message: String) : Exception","description":"com.udaan.snorql.framework.SQLMonitoringException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-exception/index.html","searchKeys":["SQLMonitoringException","class SQLMonitoringException(message: String) : Exception"]},{"name":"fun SQLMonitoringException(message: String)","description":"com.udaan.snorql.framework.SQLMonitoringException.SQLMonitoringException","location":"snorql-framework/com.udaan.snorql.framework/-s-q-l-monitoring-exception/-s-q-l-monitoring-exception.html","searchKeys":["SQLMonitoringException","fun SQLMonitoringException(message: String)"]},{"name":"interface Connection","description":"com.udaan.snorql.framework.metric.Connection","location":"snorql-framework/com.udaan.snorql.framework.metric/-connection/index.html","searchKeys":["Connection","interface Connection"]},{"name":"abstract fun <T> run(databaseName: String, query: String, mapClass: Class<T>, params: Map<String, *> = emptyMap<String, String>()): List<T>","description":"com.udaan.snorql.framework.metric.Connection.run","location":"snorql-framework/com.udaan.snorql.framework.metric/-connection/run.html","searchKeys":["run","abstract fun <T> run(databaseName: String, query: String, mapClass: Class<T>, params: Map<String, *> = emptyMap<String, String>()): List<T>"]},{"name":"abstract fun storeData(databaseName: String, tableName: String, columns: List<String>, rows: List<List<Any>>)","description":"com.udaan.snorql.framework.metric.Connection.storeData","location":"snorql-framework/com.udaan.snorql.framework.metric/-connection/store-data.html","searchKeys":["storeData","abstract fun storeData(databaseName: String, tableName: String, columns: List<String>, rows: List<List<Any>>)"]},{"name":"interface IMetric<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation>","description":"com.udaan.snorql.framework.metric.IMetric","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/index.html","searchKeys":["IMetric","interface IMetric<in T : MetricInput, O : IMetricResult, R : IMetricRecommendation>"]},{"name":"open fun getMetricConfig(metricId: String): MetricConfig","description":"com.udaan.snorql.framework.metric.IMetric.getMetricConfig","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/get-metric-config.html","searchKeys":["getMetricConfig","open fun getMetricConfig(metricId: String): MetricConfig"]},{"name":"open fun getMetricRecommendations(metricInput: T, metricResult: O): R?","description":"com.udaan.snorql.framework.metric.IMetric.getMetricRecommendations","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/get-metric-recommendations.html","searchKeys":["getMetricRecommendations","open fun getMetricRecommendations(metricInput: T, metricResult: O): R?"]},{"name":"open fun getMetricResponse(metricInput: T): MetricResponse<O, R>","description":"com.udaan.snorql.framework.metric.IMetric.getMetricResponse","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/get-metric-response.html","searchKeys":["getMetricResponse","open fun getMetricResponse(metricInput: T): MetricResponse<O, R>"]},{"name":"open fun getMetricResponseMetadata(metricInput: T, metricOutput: MetricOutput<O, R>): Map<String, Any>?","description":"com.udaan.snorql.framework.metric.IMetric.getMetricResponseMetadata","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/get-metric-response-metadata.html","searchKeys":["getMetricResponseMetadata","open fun getMetricResponseMetadata(metricInput: T, metricOutput: MetricOutput<O, R>): Map<String, Any>?"]},{"name":"abstract fun getMetricResult(metricInput: T, metricConfig: MetricConfig): O","description":"com.udaan.snorql.framework.metric.IMetric.getMetricResult","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/get-metric-result.html","searchKeys":["getMetricResult","abstract fun getMetricResult(metricInput: T, metricConfig: MetricConfig): O"]},{"name":"abstract fun saveMetricResult(metricInput: MetricInput, result: IMetricResult)","description":"com.udaan.snorql.framework.metric.IMetric.saveMetricResult","location":"snorql-framework/com.udaan.snorql.framework.metric/-i-metric/save-metric-result.html","searchKeys":["saveMetricResult","abstract fun saveMetricResult(metricInput: MetricInput, result: IMetricResult)"]},{"name":"class QueryExecutor(connection: Connection)","description":"com.udaan.snorql.framework.metric.QueryExecutor","location":"snorql-framework/com.udaan.snorql.framework.metric/-query-executor/index.html","searchKeys":["QueryExecutor","class QueryExecutor(connection: Connection)"]},{"name":"fun QueryExecutor(connection: Connection)","description":"com.udaan.snorql.framework.metric.QueryExecutor.QueryExecutor","location":"snorql-framework/com.udaan.snorql.framework.metric/-query-executor/-query-executor.html","searchKeys":["QueryExecutor","fun QueryExecutor(connection: Connection)"]},{"name":"val connection: Connection","description":"com.udaan.snorql.framework.metric.QueryExecutor.connection","location":"snorql-framework/com.udaan.snorql.framework.metric/-query-executor/connection.html","searchKeys":["connection","val connection: Connection"]},{"name":"inline fun <T> execute(databaseName: String, query: String, params: Map<String, *> = mapOf<String, Any>()): List<T>","description":"com.udaan.snorql.framework.metric.QueryExecutor.execute","location":"snorql-framework/com.udaan.snorql.framework.metric/-query-executor/execute.html","searchKeys":["execute","inline fun <T> execute(databaseName: String, query: String, params: Map<String, *> = mapOf<String, Any>()): List<T>"]},{"name":"fun persistData(databaseName: String, tableName: String, columns: List<String>, rows: List<List<Any>>)","description":"com.udaan.snorql.framework.metric.QueryExecutor.persistData","location":"snorql-framework/com.udaan.snorql.framework.metric/-query-executor/persist-data.html","searchKeys":["persistData","fun persistData(databaseName: String, tableName: String, columns: List<String>, rows: List<List<Any>>)"]},{"name":"object SqlMetricManager","description":"com.udaan.snorql.framework.metric.SqlMetricManager","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/index.html","searchKeys":["SqlMetricManager","object SqlMetricManager"]},{"name":"fun addMetric(metricId: String, instance: IMetric<*, *, *>)","description":"com.udaan.snorql.framework.metric.SqlMetricManager.addMetric","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/add-metric.html","searchKeys":["addMetric","fun addMetric(metricId: String, instance: IMetric<*, *, *>)"]},{"name":"val configuration: Configuration","description":"com.udaan.snorql.framework.metric.SqlMetricManager.configuration","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/configuration.html","searchKeys":["configuration","val configuration: Configuration"]},{"name":"fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> getMetric(metricId: String, metricInput: T): MetricResponse<*, *>","description":"com.udaan.snorql.framework.metric.SqlMetricManager.getMetric","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/get-metric.html","searchKeys":["getMetric","fun <T : MetricInput, O : IMetricResult, V : IMetricRecommendation> getMetric(metricId: String, metricInput: T): MetricResponse<*, *>"]},{"name":"val logger: Logger","description":"com.udaan.snorql.framework.metric.SqlMetricManager.logger","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/logger.html","searchKeys":["logger","val logger: Logger"]},{"name":"fun <R : Any> R.logger(): Lazy<Logger>","description":"com.udaan.snorql.framework.metric.logger","location":"snorql-framework/com.udaan.snorql.framework.metric/logger.html","searchKeys":["logger","fun <R : Any> R.logger(): Lazy<Logger>"]},{"name":"val queryExecutor: QueryExecutor","description":"com.udaan.snorql.framework.metric.SqlMetricManager.queryExecutor","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/query-executor.html","searchKeys":["queryExecutor","val queryExecutor: QueryExecutor"]},{"name":"fun setConnection(connection: Connection)","description":"com.udaan.snorql.framework.metric.SqlMetricManager.setConnection","location":"snorql-framework/com.udaan.snorql.framework.metric/-sql-metric-manager/set-connection.html","searchKeys":["setConnection","fun setConnection(connection: Connection)"]},{"name":"class Configuration(configMap: Map<String, MetricConfig>)","description":"com.udaan.snorql.framework.models.Configuration","location":"snorql-framework/com.udaan.snorql.framework.models/-configuration/index.html","searchKeys":["Configuration","class Configuration(configMap: Map<String, MetricConfig>)"]},{"name":"fun Configuration(configMap: Map<String, MetricConfig>)","description":"com.udaan.snorql.framework.models.Configuration.Configuration","location":"snorql-framework/com.udaan.snorql.framework.models/-configuration/-configuration.html","searchKeys":["Configuration","fun Configuration(configMap: Map<String, MetricConfig>)"]},{"name":"fun get(metricId: String): MetricConfig","description":"com.udaan.snorql.framework.models.Configuration.get","location":"snorql-framework/com.udaan.snorql.framework.models/-configuration/get.html","searchKeys":["get","fun get(metricId: String): MetricConfig"]},{"name":"abstract class IMetricRecommendation","description":"com.udaan.snorql.framework.models.IMetricRecommendation","location":"snorql-framework/com.udaan.snorql.framework.models/-i-metric-recommendation/index.html","searchKeys":["IMetricRecommendation","abstract class IMetricRecommendation"]},{"name":"fun IMetricRecommendation()","description":"com.udaan.snorql.framework.models.IMetricRecommendation.IMetricRecommendation","location":"snorql-framework/com.udaan.snorql.framework.models/-i-metric-recommendation/-i-metric-recommendation.html","searchKeys":["IMetricRecommendation","fun IMetricRecommendation()"]},{"name":"abstract class IMetricResult","description":"com.udaan.snorql.framework.models.IMetricResult","location":"snorql-framework/com.udaan.snorql.framework.models/-i-metric-result/index.html","searchKeys":["IMetricResult","abstract class IMetricResult"]},{"name":"fun IMetricResult()","description":"com.udaan.snorql.framework.models.IMetricResult.IMetricResult","location":"snorql-framework/com.udaan.snorql.framework.models/-i-metric-result/-i-metric-result.html","searchKeys":["IMetricResult","fun IMetricResult()"]},{"name":"data class MetricConfig(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean)","description":"com.udaan.snorql.framework.models.MetricConfig","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/index.html","searchKeys":["MetricConfig","data class MetricConfig(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean)"]},{"name":"fun MetricConfig(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean)","description":"com.udaan.snorql.framework.models.MetricConfig.MetricConfig","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/-metric-config.html","searchKeys":["MetricConfig","fun MetricConfig(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean)"]},{"name":"operator fun component1(): Map<String, String>","description":"com.udaan.snorql.framework.models.MetricConfig.component1","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/component1.html","searchKeys":["component1","operator fun component1(): Map<String, String>"]},{"name":"operator fun component1(): T","description":"com.udaan.snorql.framework.models.MetricOutput.component1","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/component1.html","searchKeys":["component1","operator fun component1(): T"]},{"name":"operator fun component1(): MetricInput","description":"com.udaan.snorql.framework.models.MetricResponse.component1","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/component1.html","searchKeys":["component1","operator fun component1(): MetricInput"]},{"name":"operator fun component2(): Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.component2","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/component2.html","searchKeys":["component2","operator fun component2(): Boolean"]},{"name":"operator fun component2(): V?","description":"com.udaan.snorql.framework.models.MetricOutput.component2","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/component2.html","searchKeys":["component2","operator fun component2(): V?"]},{"name":"operator fun component2(): MetricOutput<T, V>","description":"com.udaan.snorql.framework.models.MetricResponse.component2","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/component2.html","searchKeys":["component2","operator fun component2(): MetricOutput<T, V>"]},{"name":"operator fun component3(): Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.component3","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/component3.html","searchKeys":["component3","operator fun component3(): Boolean"]},{"name":"operator fun component3(): Map<String, Any>?","description":"com.udaan.snorql.framework.models.MetricResponse.component3","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/component3.html","searchKeys":["component3","operator fun component3(): Map<String, Any>?"]},{"name":"operator fun component4(): Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.component4","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/component4.html","searchKeys":["component4","operator fun component4(): Boolean"]},{"name":"fun copy(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean): MetricConfig","description":"com.udaan.snorql.framework.models.MetricConfig.copy","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/copy.html","searchKeys":["copy","fun copy(queries: Map<String, String>, supportsHistorical: Boolean, supportsRealTime: Boolean, isParameterized: Boolean): MetricConfig"]},{"name":"fun copy(result: T, recommendation: V?): MetricOutput<T, V>","description":"com.udaan.snorql.framework.models.MetricOutput.copy","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/copy.html","searchKeys":["copy","fun copy(result: T, recommendation: V?): MetricOutput<T, V>"]},{"name":"fun copy(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>? = null): MetricResponse<T, V>","description":"com.udaan.snorql.framework.models.MetricResponse.copy","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/copy.html","searchKeys":["copy","fun copy(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>? = null): MetricResponse<T, V>"]},{"name":"val isParameterized: Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.isParameterized","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/is-parameterized.html","searchKeys":["isParameterized","val isParameterized: Boolean"]},{"name":"val queries: Map<String, String>","description":"com.udaan.snorql.framework.models.MetricConfig.queries","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/queries.html","searchKeys":["queries","val queries: Map<String, String>"]},{"name":"val supportsHistorical: Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.supportsHistorical","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/supports-historical.html","searchKeys":["supportsHistorical","val supportsHistorical: Boolean"]},{"name":"val supportsRealTime: Boolean","description":"com.udaan.snorql.framework.models.MetricConfig.supportsRealTime","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-config/supports-real-time.html","searchKeys":["supportsRealTime","val supportsRealTime: Boolean"]},{"name":"abstract class MetricInput","description":"com.udaan.snorql.framework.models.MetricInput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/index.html","searchKeys":["MetricInput","abstract class MetricInput"]},{"name":"fun MetricInput()","description":"com.udaan.snorql.framework.models.MetricInput.MetricInput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/-metric-input.html","searchKeys":["MetricInput","fun MetricInput()"]},{"name":"abstract val databaseName: String","description":"com.udaan.snorql.framework.models.MetricInput.databaseName","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/database-name.html","searchKeys":["databaseName","abstract val databaseName: String"]},{"name":"val from: Timestamp? = null","description":"com.udaan.snorql.framework.models.MetricInput.from","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/from.html","searchKeys":["from","val from: Timestamp? = null"]},{"name":"abstract val metricId: String","description":"com.udaan.snorql.framework.models.MetricInput.metricId","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/metric-id.html","searchKeys":["metricId","abstract val metricId: String"]},{"name":"abstract val metricPeriod: MetricPeriod","description":"com.udaan.snorql.framework.models.MetricInput.metricPeriod","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/metric-period.html","searchKeys":["metricPeriod","abstract val metricPeriod: MetricPeriod"]},{"name":"val recommendationRequired: Boolean = false","description":"com.udaan.snorql.framework.models.MetricInput.recommendationRequired","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/recommendation-required.html","searchKeys":["recommendationRequired","val recommendationRequired: Boolean = false"]},{"name":"val to: Timestamp? = null","description":"com.udaan.snorql.framework.models.MetricInput.to","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-input/to.html","searchKeys":["to","val to: Timestamp? = null"]},{"name":"data class MetricOutput<T : IMetricResult, V : IMetricRecommendation>(result: T, recommendation: V?)","description":"com.udaan.snorql.framework.models.MetricOutput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/index.html","searchKeys":["MetricOutput","data class MetricOutput<T : IMetricResult, V : IMetricRecommendation>(result: T, recommendation: V?)"]},{"name":"fun <T : IMetricResult, V : IMetricRecommendation> MetricOutput(result: T, recommendation: V?)","description":"com.udaan.snorql.framework.models.MetricOutput.MetricOutput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/-metric-output.html","searchKeys":["MetricOutput","fun <T : IMetricResult, V : IMetricRecommendation> MetricOutput(result: T, recommendation: V?)"]},{"name":"val recommendation: V?","description":"com.udaan.snorql.framework.models.MetricOutput.recommendation","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/recommendation.html","searchKeys":["recommendation","val recommendation: V?"]},{"name":"val result: T","description":"com.udaan.snorql.framework.models.MetricOutput.result","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-output/result.html","searchKeys":["result","val result: T"]},{"name":"enum MetricPeriod : Enum<MetricPeriod> ","description":"com.udaan.snorql.framework.models.MetricPeriod","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/index.html","searchKeys":["MetricPeriod","enum MetricPeriod : Enum<MetricPeriod> "]},{"name":"HISTORICAL()","description":"com.udaan.snorql.framework.models.MetricPeriod.HISTORICAL","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-h-i-s-t-o-r-i-c-a-l/index.html","searchKeys":["HISTORICAL","HISTORICAL()"]},{"name":"val name: String","description":"com.udaan.snorql.framework.models.MetricPeriod.HISTORICAL.name","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-h-i-s-t-o-r-i-c-a-l/name.html","searchKeys":["name","val name: String"]},{"name":"val name: String","description":"com.udaan.snorql.framework.models.MetricPeriod.REAL_TIME.name","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-r-e-a-l_-t-i-m-e/name.html","searchKeys":["name","val name: String"]},{"name":"val ordinal: Int","description":"com.udaan.snorql.framework.models.MetricPeriod.HISTORICAL.ordinal","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-h-i-s-t-o-r-i-c-a-l/ordinal.html","searchKeys":["ordinal","val ordinal: Int"]},{"name":"val ordinal: Int","description":"com.udaan.snorql.framework.models.MetricPeriod.REAL_TIME.ordinal","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-r-e-a-l_-t-i-m-e/ordinal.html","searchKeys":["ordinal","val ordinal: Int"]},{"name":"REAL_TIME()","description":"com.udaan.snorql.framework.models.MetricPeriod.REAL_TIME","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-period/-r-e-a-l_-t-i-m-e/index.html","searchKeys":["REAL_TIME","REAL_TIME()"]},{"name":"data class MetricResponse<T : IMetricResult, V : IMetricRecommendation>(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>?)","description":"com.udaan.snorql.framework.models.MetricResponse","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/index.html","searchKeys":["MetricResponse","data class MetricResponse<T : IMetricResult, V : IMetricRecommendation>(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>?)"]},{"name":"fun <T : IMetricResult, V : IMetricRecommendation> MetricResponse(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>? = null)","description":"com.udaan.snorql.framework.models.MetricResponse.MetricResponse","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/-metric-response.html","searchKeys":["MetricResponse","fun <T : IMetricResult, V : IMetricRecommendation> MetricResponse(metricInput: MetricInput, metricOutput: MetricOutput<T, V>, metadata: Map<String, Any>? = null)"]},{"name":"val metadata: Map<String, Any>? = null","description":"com.udaan.snorql.framework.models.MetricResponse.metadata","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/metadata.html","searchKeys":["metadata","val metadata: Map<String, Any>? = null"]},{"name":"val metricInput: MetricInput","description":"com.udaan.snorql.framework.models.MetricResponse.metricInput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/metric-input.html","searchKeys":["metricInput","val metricInput: MetricInput"]},{"name":"val metricOutput: MetricOutput<T, V>","description":"com.udaan.snorql.framework.models.MetricResponse.metricOutput","location":"snorql-framework/com.udaan.snorql.framework.models/-metric-response/metric-output.html","searchKeys":["metricOutput","val metricOutput: MetricOutput<T, V>"]}]
