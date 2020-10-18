package com.gmaniliapp.notes.data.repository

import android.app.Application
import com.gmaniliapp.notes.data.local.dao.NoteDAO
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.data.remote.NoteApi
import com.gmaniliapp.notes.util.Resource
import com.gmaniliapp.notes.util.isInternetConnectionEnabled
import com.gmaniliapp.notes.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDAO: NoteDAO,
    private val noteApi: NoteApi,
    private val context: Application
) {

    suspend fun insertNote(note: Note) {
        /*
        val response = try {
            noteApi.createNote(note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note)
        } else {
            // TODO: Handle error response
        } */
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
        /*
        val response = try {
            noteApi.updateNote(note)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.insertNote(note)
        } else {
            // TODO: Handle error response
        } */
    }

    suspend fun deleteNoteById(noteId: String) {
        /*
        val response = try {
            noteApi.deleteNote(DeleteNoteRequest(noteId))
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
            noteDAO.deleteNoteById(noteId)
        } else {
            noteDAO.updateNoteDeletedState(noteId, true)
        } */
    }

    private suspend fun syncNotes(notes: List<Note>) {
        /*
        val response = try {
            noteApi.syncNotes(notes)
        } catch (e: Exception) {
            null
        }

        if (response != null && response.isSuccessful) {
        } */
    }

}