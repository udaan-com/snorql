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

package com.udaan.snorql.extensions.storage

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StorageEnumsTest {

    @Test
    fun testGetId() {
        assertNotNull(StorageEnums.DB)
        assertNotNull(StorageEnums.DB_GROWTH)
        assertNotNull(StorageEnums.DB_INDEX)
        assertNotNull(StorageEnums.PVS)
        assertNotNull(StorageEnums.DB_TABLES)
        assertNotNull(StorageEnums.TABLE)
        assertNotNull(StorageEnums.TABLE_SCHEMA)
        assertNotNull(StorageEnums.TABLE_UNUSED_INDEX)

        assertEquals("storage_db",StorageEnums.DB.getId())
        assertEquals("storage_dbGrowth",StorageEnums.DB_GROWTH.getId())
        assertEquals("storage_dbIndex",StorageEnums.DB_INDEX.getId())
        assertEquals("storage_pvs",StorageEnums.PVS.getId())
        assertEquals("storage_dbTables",StorageEnums.DB_TABLES.getId())
        assertEquals("storage_table",StorageEnums.TABLE.getId())
        assertEquals("storage_tableSchema",StorageEnums.TABLE_SCHEMA.getId())
        assertEquals("storage_tableUnusedIndex",StorageEnums.TABLE_UNUSED_INDEX.getId())
    }

}