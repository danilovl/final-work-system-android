package com.finalworksystem

import com.finalworksystem.data.util.ApiResponseUtil
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.Assert.*
import retrofit2.Response

class ApiResponseUtilTest {

    @Test
    fun `handleApiResponse should return success for 204 status code`() {
        val response = Response.success(204, null as Any?)
        val result = ApiResponseUtil.handleApiResponse(response)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `handleApiResponse should return failure for 404 status code with custom message`() {
        val response = Response.error<Any>(404, "".toResponseBody())
        val result = ApiResponseUtil.handleApiResponse(
            response,
            notFoundMessage = "Custom not found message"
        )

        assertTrue(result.isFailure)
        assertEquals("Custom not found message", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleApiResponse should return failure for 403 status code with custom message`() {
        val response = Response.error<Any>(403, "".toResponseBody())
        val result = ApiResponseUtil.handleApiResponse(
            response,
            accessDeniedMessage = "Custom access denied message"
        )

        assertTrue(result.isFailure)
        assertEquals("Custom access denied message", result.exceptionOrNull()?.message)
    }

    @Test
    fun `handleApiResponse should return failure for other error codes with operation name`() {
        val response = Response.error<Any>(500, "Internal Server Error".toResponseBody())
        val result = ApiResponseUtil.handleApiResponse(
            response,
            operationName = "test operation"
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Failed to perform test operation") == true)
        assertTrue(result.exceptionOrNull()?.message?.contains("500") == true)
    }

    @Test
    fun `handleApiResponse should return success for other 2xx status codes`() {
        val response = Response.success(200, null as Any?)
        val result = ApiResponseUtil.handleApiResponse(response)

        assertTrue(result.isSuccess)
    }
}
