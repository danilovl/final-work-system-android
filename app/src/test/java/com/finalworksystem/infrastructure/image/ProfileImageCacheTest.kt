package com.finalworksystem.infrastructure.image

import com.finalworksystem.infrastructure.api.ApiConstant
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileImageCacheTest {

    @Test
    fun `profile image url points to the user image endpoint`() {
        val url = ProfileImageCache.url(42).toHttpUrl()
        val baseUrl = ApiConstant.BASE_URL.toHttpUrl()

        assertEquals(baseUrl.host, url.host)
        assertEquals(baseUrl.port, url.port)
        assertEquals("/api/key/users/42/profile/image", url.encodedPath)
    }
}
