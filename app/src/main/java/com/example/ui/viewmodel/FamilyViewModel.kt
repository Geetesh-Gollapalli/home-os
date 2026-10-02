package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.FamilyContact
import com.example.data.repository.FamilyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FamilyUiState(
    val contacts: List<FamilyContact> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val pendingCallContact: FamilyContact? = null
)

class FamilyViewModel(
    private val repository: FamilyRepository
) : ViewModel() {

    private val _isAddDialogOpen = MutableStateFlow(false)
    private val _pendingCallContact = MutableStateFlow<FamilyContact?>(null)

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    val uiState: StateFlow<FamilyUiState> = combine(
        repository.allContacts,
        _isAddDialogOpen,
        _pendingCallContact
    ) { contacts, isDialogOpen, pendingCall ->
        FamilyUiState(
            contacts = contacts,
            isAddDialogOpen = isDialogOpen,
            pendingCallContact = pendingCall
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FamilyUiState()
    )

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun initiateCall(contact: FamilyContact) {
        _pendingCallContact.value = contact
    }

    fun dismissCallConfirmation() {
        _pendingCallContact.value = null
    }

    fun addContact(name: String, relationship: String, phoneNumber: String) {
        if (name.isBlank() || phoneNumber.isBlank()) return
        viewModelScope.launch {
            val colors = listOf("#5B5BD6", "#2E9B68", "#D99100", "#9C27B0", "#00838F")
            val chosenColor = colors.random()
            repository.insertContact(
                FamilyContact(
                    name = name.trim(),
                    relationship = relationship.trim().ifBlank { "Family" },
                    phoneNumber = phoneNumber.trim(),
                    avatarColorHex = chosenColor
                )
            )
            _feedbackEvents.emit("${name.trim()} added to family contacts")
            closeAddDialog()
        }
    }

    fun deleteContact(contact: FamilyContact) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            _feedbackEvents.emit("Contact removed")
        }
    }

    companion object {
        fun provideFactory(repository: FamilyRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FamilyViewModel(repository) as T
                }
            }
    }
}
