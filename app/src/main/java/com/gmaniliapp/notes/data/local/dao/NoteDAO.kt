package com.gmaniliapp.notes.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gmaniliapp.notes.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM NOTE WHERE ID = :id")
    suspend fun deleteNoteById(id: String)

    @Query("DELETE FROM NOTE WHERE IS_SYNC = 1")
    suspend fun deleteAllSyncNotes()

    @Query("SELECT * FROM NOTE WHERE ID = :id")
    fun observeNoteById(id: String): LiveData<Note>

    @Query("SELECT * FROM NOTE WHERE ID = :id")
    suspend fun selectNoteById(id: String): Note?

    @Query("SELECT * FROM NOTE ORDER BY DATE DESC")
    fun selectAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM NOTE WHERE IS_SYNC = 0")
    suspend fun selectAllNotSyncNotes(): List<Note>
}