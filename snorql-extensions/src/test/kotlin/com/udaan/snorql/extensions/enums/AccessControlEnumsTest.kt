package com.udaan.snorql.extensions.enums

import com.udaan.snorql.extensions.accesscontrol.AccessControlEnums
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AccessControlEnumsTest {

    @Test
    fun testGetId() {
        assertNotNull(AccessControlEnums.USER_ROLE)

        assertEquals("accessControl_userRole", AccessControlEnums.USER_ROLE.getId())
    }
}