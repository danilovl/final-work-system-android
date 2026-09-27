package com.finalworksystem

import com.finalworksystem.infrastructure.api.ApiConstant
import com.finalworksystem.infrastructure.api.ApiEndpoint
import org.junit.Assert.assertEquals
import org.junit.Test

class WorkEndpointsTest {

    @Test
    fun testWorkCreateEndpointsMatchSpecification() {
        println("[DEBUG_LOG] Testing API Endpoint constants match specification")

        assertEquals("/api/key/works/create", ApiEndpoint.Work.POST_CREATE)
        assertEquals("/api/key/users/search/{type}", ApiEndpoint.User.GET_SEARCH)
        assertEquals("/api/key/work-types", ApiEndpoint.Work.GET_TYPES)
        assertEquals("/api/key/work-statuses", ApiEndpoint.Work.GET_STATUSES)
        assertEquals("/api/key/work-categories", ApiEndpoint.Work.GET_CATEGORIES)

        assertEquals("/api/key/works/create", ApiConstant.API_KEY_WORK_CREATE)
        assertEquals("/api/key/users/search/{type}", ApiConstant.API_KEY_USER_SEARCH)
        assertEquals("/api/key/work-types", ApiConstant.API_KEY_WORK_TYPES)
        assertEquals("/api/key/work-statuses", ApiConstant.API_KEY_WORK_STATUSES)
        assertEquals("/api/key/work-categories", ApiConstant.API_KEY_WORK_CATEGORIES)

        println("[DEBUG_LOG] All endpoint constants successfully verified")
    }
}
