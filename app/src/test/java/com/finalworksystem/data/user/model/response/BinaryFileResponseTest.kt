package com.finalworksystem.data.user.model.response

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class BinaryFileResponseTest {

    @Test
    fun testBinaryFileResponseToDomainModel() {
        println("[DEBUG_LOG] Testing BinaryFileResponse to domain model conversion")

        val imageData = "fake-image-data".toByteArray()
        val responseBody = imageData.toResponseBody("image/jpeg".toMediaType())

        val binaryFileResponse = BinaryFileResponse(
            fileName = "profile.jpg",
            contentType = "image/jpeg",
            contentLength = imageData.size.toLong(),
            responseBody = responseBody
        )

        try {
            val userProfileImage = binaryFileResponse.toDomainModel()

            assertEquals("profile.jpg", userProfileImage.fileName)
            assertEquals("image/jpeg", userProfileImage.contentType)
            assertEquals(imageData.size.toLong(), userProfileImage.contentLength)
            assertArrayEquals(imageData, userProfileImage.imageData)

            println("[DEBUG_LOG] BinaryFileResponse to domain model conversion successful!")
            println("[DEBUG_LOG] FileName: ${userProfileImage.fileName}")
            println("[DEBUG_LOG] ContentType: ${userProfileImage.contentType}")
            println("[DEBUG_LOG] ContentLength: ${userProfileImage.contentLength}")
            println("[DEBUG_LOG] ImageData size: ${userProfileImage.imageData.size}")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Conversion failed: ${e.message}")
            e.printStackTrace()
            fail("BinaryFileResponse to domain model conversion should not fail: ${e.message}")
        }
    }

    @Test
    fun testBinaryFileResponseWithNullValues() {
        println("[DEBUG_LOG] Testing BinaryFileResponse with null values")

        val imageData = "test-data".toByteArray()
        val responseBody = imageData.toResponseBody("application/octet-stream".toMediaType())

        val binaryFileResponse = BinaryFileResponse(
            fileName = null,
            contentType = null,
            contentLength = null,
            responseBody = responseBody
        )

        try {
            val userProfileImage = binaryFileResponse.toDomainModel()

            assertNull(userProfileImage.fileName)
            assertNull(userProfileImage.contentType)
            assertNull(userProfileImage.contentLength)
            assertArrayEquals(imageData, userProfileImage.imageData)

            println("[DEBUG_LOG] BinaryFileResponse with null values conversion successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Conversion with null values failed: ${e.message}")
            e.printStackTrace()
            fail("BinaryFileResponse conversion with null values should not fail: ${e.message}")
        }
    }

    @Test
    fun testBinaryFileResponseWithEmptyData() {
        println("[DEBUG_LOG] Testing BinaryFileResponse with empty data")

        val emptyData = ByteArray(0)
        val responseBody = emptyData.toResponseBody("image/png".toMediaType())

        val binaryFileResponse = BinaryFileResponse(
            fileName = "empty.png",
            contentType = "image/png",
            contentLength = 0L,
            responseBody = responseBody
        )

        try {
            val userProfileImage = binaryFileResponse.toDomainModel()

            assertEquals("empty.png", userProfileImage.fileName)
            assertEquals("image/png", userProfileImage.contentType)
            assertEquals(0L, userProfileImage.contentLength)
            assertEquals(0, userProfileImage.imageData.size)

            println("[DEBUG_LOG] BinaryFileResponse with empty data conversion successful!")

        } catch (e: Exception) {
            println("[DEBUG_LOG] Conversion with empty data failed: ${e.message}")
            e.printStackTrace()
            fail("BinaryFileResponse conversion with empty data should not fail: ${e.message}")
        }
    }
}
