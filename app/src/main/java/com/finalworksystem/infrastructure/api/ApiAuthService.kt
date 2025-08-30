package com.finalworksystem.infrastructure.api

import com.finalworksystem.data.auth.model.LoginRequest
import com.finalworksystem.data.auth.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiAuthService {
    @POST(ApiConstant.API_PUBLIC_SECURITY_GENERATE_TOKEN)
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>
}
