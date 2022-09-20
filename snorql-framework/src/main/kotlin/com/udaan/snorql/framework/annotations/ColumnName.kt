package com.udaan.snorql.framework.annotations

/**
 * Annotation to explicitly provide mapping name for sql columns returned the db
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class SnorqlColumnName(val colName: String)
