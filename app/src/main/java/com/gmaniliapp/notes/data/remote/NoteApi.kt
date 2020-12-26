package com.gmaniliapp.notes.data.remote

import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.request.AddOwnerRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface NoteApi {

    /* Create */

    @POST("/rest/v1/notes")
    suspend fun createNote(
        @Body note: Note
    ): Response<ResponseBody>

    /* Read */

    @GET("/rest/v1/notes")
    suspend fun readNotes(): Response<List<Note>>

    /* Update */

    @PUT("/rest/v1/notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: String,
        @Body note: Note
    ): Response<ResponseBody>

    @PUT("/rest/v1/notes/sync")
    suspend fun syncNotes(
        @Body notes: List<Note>
    ): Response<ResponseBody>

    @PUT("/rest/v1/notes/{noteId}/owners")
    suspend fun addOwnerToNote(
        @Path("noteId") noteId: String,
        @Body request: AddOwnerRequest
    ): Response<ResponseBody>

    /* Delete */

    @DELETE("/rest/v1/notes/{noteId}")
    suspend fun deleteNote(
        @Path("noteId") noteId: String
    ): Response<ResponseBody>
}