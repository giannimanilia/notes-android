package com.gmaniliapp.notes.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var deleted: Boolean = false
)