package com.udaan.snorql.framework

import org.junit.Test
import kotlin.test.assertEquals

enum class DemoEnums(private val metricId:String):IMtericId {

    DEMO_ENUM1("demoEnum1"),
    DEMO_ENUM2("demoEnum2");

    override fun getId(): String {
        return "demoPrefix_" + this.metricId
    }

}

class TestIMetricId {

    @Test
    fun testGetId() {
        assertEquals("demoPrefix_demoEnum1", DemoEnums.DEMO_ENUM1.getId())
        assertEquals("DEMO_ENUM1", DemoEnums.DEMO_ENUM1.name)
        assertEquals("demoPrefix_demoEnum2", DemoEnums.DEMO_ENUM2.getId())
        assertEquals("DEMO_ENUM2", DemoEnums.DEMO_ENUM2.name)
    }

}