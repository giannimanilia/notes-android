package com.gmaniliapp.notes.data.remote

import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.request.AccountRequest
import com.gmaniliapp.notes.data.remote.request.AddOwnerRequest
import com.gmaniliapp.notes.data.remote.request.DeleteNoteRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface NoteApi {

    @POST("/rest/v1/auth/register")
    suspend fun register(
        @Body request: AccountRequest
    ): Response<ResponseBody>

    @POST("/rest/v1/auth/login")
    suspend fun login(
        @Body request: AccountRequest
    ): Response<ResponseBody>

    @POST("/rest/v1/notes")
    suspend fun createNote(
        @Body note: Note
    ): Response<ResponseBody>

    @DELETE("/rest/v1/notes")
    suspend fun deleteNote(
        @Body request: DeleteNoteRequest
    ): Response<ResponseBody>

    @PUT("/rest/v1/notes/{noteId}/owners")
    suspend fun addOwnerToNote(
        @Path("noteId") noteId: String,
        @Body request: AddOwnerRequest
    ): Response<ResponseBody>

    @GET("/rest/v1/notes")
    suspend fun readNotes(): Response<List<Note>>
}