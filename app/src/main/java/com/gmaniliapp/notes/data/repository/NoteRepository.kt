package com.gmaniliapp.notes.data.repository

import android.app.Application
import com.gmaniliapp.notes.data.local.dao.NoteDAO
import com.gmaniliapp.notes.data.remote.NoteApi
import com.gmaniliapp.notes.data.remote.request.AccountRequest
import com.gmaniliapp.notes.data.remote.response.StandardResponse
import com.gmaniliapp.notes.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDAO: NoteDAO,
    private val noteApi: NoteApi,
    private val context: Application
) {
    suspend fun register(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.register(AccountRequest(email, password))
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