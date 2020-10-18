package com.gmaniliapp.notes.data.remote

import com.gmaniliapp.notes.data.remote.request.AccountRequest
import com.gmaniliapp.notes.data.remote.response.StandardResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/rest/v1/auth/register")
    suspend fun register(
        @Body request: AccountRequest
    ): Response<StandardResponse>

    @POST("/rest/v1/auth/login")
    suspend fun login(
        @Body request: AccountRequest
    ): Response<StandardResponse>
}