package com.gmaniliapp.notes.data.repository

import com.gmaniliapp.notes.data.remote.AuthApi
import com.gmaniliapp.notes.data.remote.request.AccountRequest
import com.gmaniliapp.notes.data.remote.response.StandardResponse
import com.gmaniliapp.notes.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {

    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = authApi.register(AccountRequest(email, password))
            if (response.isSuccessful) {
                val standardResponse = response.body()
                if (standardResponse != null) {
                    Resource.success(standardResponse.message.toString())
                } else {
                    Resource.error("Error communicating with server")
                }
            } else {
                val standardResponse =
                    Gson().fromJson(response.errorBody()?.string(), StandardResponse::class.java)
                if (standardResponse != null) {
                    Resource.error(standardResponse.message.toString())
                } else {
                    Resource.error(response.message())
                }
            }
        } catch (exception: Exception) {
            Resource.error("Error communicating with server")
        }
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = authApi.login(AccountRequest(email, password))
            if (response.isSuccessful) {
                val standardResponse = response.body()
                if (standardResponse != null) {
                    Resource.success(standardResponse.message.toString())
                } else {
                    Resource.error("Error communicating with server")
                }
            } else {
                val standardResponse =
                    Gson().fromJson(response.errorBody()?.string(), StandardResponse::class.java)
                if (standardResponse != null) {
                    Resource.error(standardResponse.message.toString())
                } else {
                    Resource.error(response.message())
                }
            }
        } catch (exception: Exception) {
            Resource.error("Error communicating with server")
        }
    }
}