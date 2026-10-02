package com.example.data.repository

import com.example.data.dao.NoteDao
import com.example.data.model.NoteItem
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteItem>> = noteDao.getAllNotes()

    fun searchNotes(query: String): Flow<List<NoteItem>> = noteDao.searchNotes(query)

    suspend fun insertNote(note: NoteItem): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteItem) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteItem) = noteDao.deleteNote(note)

    suspend fun clearAll() = noteDao.clearAll()
}
