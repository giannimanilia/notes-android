package com.gmaniliapp.notes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "note")
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    val owners: List<String>,
    val color: String,
    @ColumnInfo(name = "creation_date")
    val creationDate: Long,
    @ColumnInfo(name = "update_date")
    var updateDate: Long,
    var deleted: Boolean = false
)