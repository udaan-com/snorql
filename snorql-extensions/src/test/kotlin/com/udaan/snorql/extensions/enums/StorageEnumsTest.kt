package com.udaan.snorql.extensions.enums

import com.udaan.snorql.extensions.storage.StorageEnums
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