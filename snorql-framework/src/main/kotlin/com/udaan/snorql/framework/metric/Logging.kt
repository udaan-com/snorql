/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.udaan.snorql.framework.metric

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

/**
 * Return logger for Java class, if companion object fix the name
 */
private fun <T : Any> logger(forClass: Class<T>): Logger {

    fun Logger.debug(s: () -> String) {
        if (isDebugEnabled) debug(s())
    }

    return LoggerFactory.getLogger(unwrapCompanionClass(forClass).name)
}

/**
 * unwrap companion class to enclosing class given a Java Class
 */
private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

/**
 * Function to return a lazy logger property delegate for enclosing class
 *
 * @param R enclosing class
 * @return lazy logger property delegate for enclosing class
 */
fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { logger(this.javaClass) }
}