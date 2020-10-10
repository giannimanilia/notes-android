package com.gmaniliapp.notes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import java.util.*

@Entity(tableName = "note")
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    val date: Long,
    val owners: List<String>,
    val color: String,
    @Expose(deserialize = false, serialize = false)
    @ColumnInfo(name = "is_sync")
    var isSync: Boolean = false
)