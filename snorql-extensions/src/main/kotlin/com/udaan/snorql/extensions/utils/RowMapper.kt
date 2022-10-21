package com.udaan.snorql.extensions.utils

import com.udaan.snorql.framework.annotations.SnorqlColumnName
import java.sql.ResultSet

/**
 * Extension Utility to Map [ResultSet] to [T] mapClass provided by the client
 */
fun <T> ResultSet.rowMapper(mapClass: Class<T>): List<T> {
    val outputList = mutableListOf<T>()
    val columns = getAllColsInOrder(mapClass)
    val constructor = mapClass.getDeclaredConstructor(
        *columns.map { (_, type) -> type }.toTypedArray()
    )
    while(this.next()) {
        val instanceArgs: List<Any?> = columns.map { (mappingName, type) ->
            when(type.typeName) {
                "java.lang.String" -> this.getString(mappingName) ?: null
                "int", "java.lang.Integer" -> this.getInt(mappingName)
                "java.lang.Double", "double" -> this.getDouble(mappingName) ?: null
                "boolean", "java.lang.Boolean" -> this.getBoolean(mappingName) ?: null
                "long" -> this.getLong(mappingName)
                "java.util.Date", "java.sql.Date" -> this.getDate(mappingName) ?: null
                "java.sql.Timestamp" -> this.getTimestamp(mappingName) ?: null
                "java.time.Instant" -> this.getTimestamp(mappingName)?.toInstant()
                else -> this.getObject(mappingName) ?: Any()
            }
        }
        outputList.add(
            constructor.newInstance(*instanceArgs.toTypedArray())
        )
    }
    return outputList
}

/**
 * Returns mapping name for all declared fields [T] mapClass
 * Also looks for [SnorqlColumnName] annotation to extract and use the name provided by the client
 */
private fun <T> getAllColsInOrder(mapClass: Class<T>): List<Pair<String, Class<*>>> {
    return mapClass
        .declaredFields
        .map {
            val mappingName = it.getAnnotation(SnorqlColumnName::class.java)?.colName ?: it.name
            val type = it.type
            mappingName to type
        }
}
