package com.udaan.snorql.extensions.enums

import com.udaan.snorql.extensions.performance.PerformanceEnums
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PerformanceEnumsTest {

    @Test
    fun testGetId() {
        assertNotNull(PerformanceEnums.ACTIVE_DDL)
        assertNotNull(PerformanceEnums.ACTIVE_QUERIES)
        assertNotNull(PerformanceEnums.BLOCKED_QUERIES)
        assertNotNull(PerformanceEnums.INDEX_STATS)
        assertNotNull(PerformanceEnums.LONG_RUNNING_QUERIES)

        assertEquals("performance_activeDDL", PerformanceEnums.ACTIVE_DDL.getId())
        assertEquals("performance_activeQueries", PerformanceEnums.ACTIVE_QUERIES.getId())
        assertEquals("performance_blockedQueries", PerformanceEnums.BLOCKED_QUERIES.getId())
        assertEquals("performance_indexStats", PerformanceEnums.INDEX_STATS.getId())
        assertEquals("performance_longRunningQueries", PerformanceEnums.LONG_RUNNING_QUERIES.getId())
    }
}