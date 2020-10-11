package com.gmaniliapp.notes.data.repository

import android.app.Application
import com.gmaniliapp.notes.data.local.dao.NoteDAO
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.NoteApi
import com.gmaniliapp.notes.data.remote.request.AccountRequest
import com.gmaniliapp.notes.data.remote.request.DeleteNoteRequest
import com.gmaniliapp.notes.data.remote.response.StandardResponse
import com.gmaniliapp.notes.util.Resource
import com.gmaniliapp.notes.util.isInternetConnectionEnabled
import com.gmaniliapp.notes.util.networkBoundResource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = noteApi.login(AccountRequest(email, password))
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

    suspend fun insertNote(note: Note) {
        val response = try {
            noteApi.createNote(note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note.apply { isSync = true })
        } else {
            noteDAO.insertNote(note)
        }
    }

    fun getAllNotes(): Flow<Resource<List<Note>>> {
        return networkBoundResource(
            query = {
                noteDAO.selectAllNotes()
            },
            fetch = {
                noteApi.readNotes()
            },
            saveFetchResult = { response ->
                response.body()?.let {
                    syncNotes(it)
                }
            },
            shouldFetch = {
                isInternetConnectionEnabled(context)
            }
        )
    }

    suspend fun selectNoteById(noteId: String) = noteDAO.selectNoteById(noteId)

    suspend fun updateNote(note: Note) {
        val response = try {
            noteApi.updateNote(note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note.apply { isSync = true })
        } else {
            noteDAO.insertNote(note)
        }
    }

    suspend fun deleteNoteById(noteId: String) {
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteId))
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.deleteNoteById(noteId)
        } else {
            noteDAO.updateNoteDeletedState(noteId, true)
        }
    }

    private suspend fun syncNotes(notes: List<Note>) {
        notes.forEach { }
    }

}