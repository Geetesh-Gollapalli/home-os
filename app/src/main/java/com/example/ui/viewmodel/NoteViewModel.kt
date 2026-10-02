package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.NoteItem
import com.example.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteUiState(
    val notes: List<NoteItem> = emptyList(),
    val searchQuery: String = "",
    val isAddDialogOpen: Boolean = false,
    val selectedNoteForEdit: NoteItem? = null
)

class NoteViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _isAddDialogOpen = MutableStateFlow(false)
    private val _selectedNote = MutableStateFlow<NoteItem?>(null)

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    val uiState: StateFlow<NoteUiState> = combine(
        repository.allNotes,
        _searchQuery,
        _isAddDialogOpen,
        _selectedNote
    ) { notes, query, isDialogOpen, noteForEdit ->
        val filtered = if (query.isBlank()) {
            notes
        } else {
            notes.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.content.contains(query, ignoreCase = true)
            }
        }
        NoteUiState(
            notes = filtered,
            searchQuery = query,
            isAddDialogOpen = isDialogOpen,
            selectedNoteForEdit = noteForEdit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog(note: NoteItem? = null) {
        _selectedNote.value = note
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _selectedNote.value = null
        _isAddDialogOpen.value = false
    }

    fun saveNote(title: String, content: String) {
        if (title.isBlank() && content.isBlank()) return
        viewModelScope.launch {
            val noteToSave = _selectedNote.value
            val cleanTitle = if (title.isNotBlank()) title.trim() else content.take(30).trim()
            if (noteToSave != null) {
                repository.updateNote(
                    noteToSave.copy(
                        title = cleanTitle,
                        content = content.trim(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _feedbackEvents.emit("Note updated")
            } else {
                repository.insertNote(
                    NoteItem(
                        title = cleanTitle,
                        content = content.trim(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _feedbackEvents.emit("Note saved")
            }
            closeAddDialog()
        }
    }

    fun deleteNote(note: NoteItem) {
        viewModelScope.launch {
            repository.deleteNote(note)
            _feedbackEvents.emit("Note deleted")
        }
    }

    companion object {
        fun provideFactory(repository: NoteRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(repository) as T
                }
            }
    }
}
