package com.udaan.snorql.framework.models

import org.junit.Test
import kotlin.test.assertNotNull

class TestMetricPeriod {

    @Test
    fun testMetricPeriod() {
        assertNotNull(MetricPeriod.REAL_TIME)
        assertNotNull(MetricPeriod.HISTORICAL)
    }
}