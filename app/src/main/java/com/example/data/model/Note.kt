package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

typealias NoteItem = Note
