package com.gmaniliapp.notes.data.repository

import android.app.Application
import com.gmaniliapp.notes.data.local.dao.NoteDAO
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.NoteApi
import com.gmaniliapp.notes.data.remote.request.AddOwnerRequest
import com.gmaniliapp.notes.util.Resource
import com.gmaniliapp.notes.util.isInternetConnectionEnabled
import com.gmaniliapp.notes.util.networkSyncResource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDAO: NoteDAO,
    private val noteApi: NoteApi,
    private val context: Application
) {

    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.createNote(note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note)
        } else {
            // TODO: Handle error response
        }
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkSyncResource(
            query = {
                noteDAO.selectAllNotes()
            },
            sync = { notes ->
                noteApi.syncNotes(notes)
            },
            processSyncResult = { response ->
                processSyncResult(response)
            },
            shouldSync = {
                isInternetConnectionEnabled(context)
            }
        )
    }

    suspend fun selectNoteById(noteId: String) = noteDAO.selectNoteById(noteId)

    suspend fun updateNote(note: Note) {
        val response = try {
            noteApi.updateNote(note.id, note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note)
        } else {
            // TODO: Handle error response
        }
    }

    suspend fun addOwnerToNote(owner: String, noteID: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.addOwnerToNote(noteID, AddOwnerRequest(owner))
            if (response.isSuccessful) {
                // TODO: Handle ok response
                Resource.success("Ok")
            } else {
                // TODO: Handle error response
                Resource.error("Error communicating with server")
            }
        } catch (e: Exception) {
            Resource.error("Error communicating with server")
        }
    }

    suspend fun deleteNoteById(noteId: String) {
        val response = try {
            noteApi.deleteNote(noteId)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.deleteNoteById(noteId)
        } else {
            // TODO: Handle error response
        }
    }

    private suspend fun processSyncResult(response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            val notes: List<Note> =
                Gson().fromJson(response.body()?.string(), object : TypeToken<List<Note>>() {}.type)
            notes.forEach { note ->
                if (note.deleted) {
                    noteDAO.deleteNoteById(note.id)
                } else {
                    noteDAO.insertNote(note)
                }
            }
        } else {
            // TODO: Handle error response
        }
    }

}