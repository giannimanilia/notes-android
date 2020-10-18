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

    /* Create */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    /* Read */

    @Query("SELECT * FROM NOTE WHERE ID = :id AND DELETED = 0")
    fun observeNoteById(id: String): LiveData<Note>

    @Query("SELECT * FROM NOTE WHERE ID = :id AND DELETED = 0")
    suspend fun selectNoteById(id: String): Note?

    @Query("SELECT * FROM NOTE WHERE DELETED = 0 ORDER BY DATE DESC")
    fun selectAllNotes(): Flow<List<Note>>

    /* Update */

    @Query("UPDATE NOTE SET DELETED = :deleted WHERE ID = :id")
    suspend fun updateNoteDeletedState(id: String, deleted: Boolean)

    /* Delete */

    @Query("DELETE FROM NOTE WHERE ID = :id")
    suspend fun deleteNoteById(id: String)

    @Query("DELETE FROM NOTE WHERE DELETED = :deleted")
    suspend fun deleteNoteByDeletedState(deleted: Boolean)
}