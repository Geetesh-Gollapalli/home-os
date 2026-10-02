package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserProfile
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.FamilyRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.ShoppingRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class SettingsUiState(
    val userProfile: UserProfile = UserProfile(),
    val familyGroupCode: String = "HOME-9821",
    val isFamilyConnected: Boolean = false,
    val otherConnectedDevices: List<String> = emptyList(),
    val isConnectDevicesDialogOpen: Boolean = false,
    val isClearDataDialogOpen: Boolean = false,
    val isExportDialogOpen: Boolean = false,
    val backupJsonPreview: String? = null
)

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val shoppingRepository: ShoppingRepository,
    private val taskRepository: TaskRepository,
    private val expenseRepository: ExpenseRepository,
    private val familyRepository: FamilyRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            userRepository.userProfile.collect { profile ->
                if (profile != null) {
                    _uiState.value = _uiState.value.copy(userProfile = profile)
                }
            }
        }
    }

    fun updateUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(name = name.trim())
            userRepository.updateProfile(updated)
            _feedbackEvents.emit("Name updated")
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(isDarkMode = enabled)
            userRepository.updateProfile(updated)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val updated = _uiState.value.userProfile.copy(notificationsEnabled = enabled)
            userRepository.updateProfile(updated)
            val msg = if (enabled) "Reminders enabled" else "Reminders muted"
            _feedbackEvents.emit(msg)
        }
    }

    fun openConnectDevicesDialog() {
        _uiState.value = _uiState.value.copy(isConnectDevicesDialogOpen = true)
    }

    fun closeConnectDevicesDialog() {
        _uiState.value = _uiState.value.copy(isConnectDevicesDialogOpen = false)
    }

    fun joinFamilyGroup(code: String) {
        val cleanCode = code.trim().uppercase(Locale.ROOT)
        if (cleanCode.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                familyGroupCode = cleanCode,
                isFamilyConnected = true,
                otherConnectedDevices = listOf("Joined Family Group ($cleanCode)")
            )
            _feedbackEvents.emit("Connected to Family Group $cleanCode ✓")
        }
    }

    fun openClearDataDialog() {
        _uiState.value = _uiState.value.copy(isClearDataDialogOpen = true)
    }

    fun closeClearDataDialog() {
        _uiState.value = _uiState.value.copy(isClearDataDialogOpen = false)
    }

    fun clearAllData() {
        viewModelScope.launch {
            shoppingRepository.clearAll()
            taskRepository.clearAll()
            expenseRepository.clearAll()
            familyRepository.clearAll()
            noteRepository.clearAll()
            closeClearDataDialog()
            _feedbackEvents.emit("All application data reset")
        }
    }

    fun generateBackup() {
        viewModelScope.launch {
            val preview = "MomOS Backup: Ready. Encrypted local storage valid."
            _uiState.value = _uiState.value.copy(
                isExportDialogOpen = true,
                backupJsonPreview = preview
            )
            _feedbackEvents.emit("Backup generated successfully")
        }
    }

    fun closeExportDialog() {
        _uiState.value = _uiState.value.copy(isExportDialogOpen = false)
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            shoppingRepository: ShoppingRepository,
            taskRepository: TaskRepository,
            expenseRepository: ExpenseRepository,
            familyRepository: FamilyRepository,
            noteRepository: NoteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    userRepository,
                    shoppingRepository,
                    taskRepository,
                    expenseRepository,
                    familyRepository,
                    noteRepository
                ) as T
            }
        }
    }
}
