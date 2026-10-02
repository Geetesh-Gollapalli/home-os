package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai.CommandExecutionResult
import com.example.ai.VoiceCommandIntent
import com.example.ai.VoiceCommandParser
import com.example.data.model.ExpenseItem
import com.example.data.model.FamilyContact
import com.example.data.model.NoteItem
import com.example.data.model.ShoppingItem
import com.example.data.model.TaskItem
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val greeting: String = "Good morning",
    val userName: String = "User",
    val isBirthday: Boolean = false,
    val todayTasks: List<TaskItem> = emptyList(),
    val activeShoppingCount: Int = 0,
    val monthlyExpenseTotal: Double = 0.0,
    val familyContacts: List<FamilyContact> = emptyList(),
    val isVoiceSheetVisible: Boolean = false,
    val pendingConfirmationIntent: VoiceCommandIntent? = null,
    val confirmationPrompt: String? = null
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val shoppingRepository: ShoppingRepository,
    private val expenseRepository: ExpenseRepository,
    private val familyRepository: FamilyRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    init {
        updateGreeting()
        observeData()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }

        // Check subtle birthday touch (current month-day)
        val currentMd = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
        _uiState.value = _uiState.value.copy(
            greeting = greeting,
            isBirthday = (currentMd == "10-01")
        )
    }

    private fun observeData() {
        viewModelScope.launch {
            userRepository.userProfile.collect { profile ->
                if (profile != null) {
                    val currentMd = SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date())
                    _uiState.value = _uiState.value.copy(
                        userName = profile.name,
                        isBirthday = (profile.birthday == currentMd)
                    )
                }
            }
        }

        viewModelScope.launch {
            taskRepository.getTodayTasks().collect { tasks ->
                _uiState.value = _uiState.value.copy(todayTasks = tasks)
            }
        }

        viewModelScope.launch {
            shoppingRepository.activeItems.collect { items ->
                _uiState.value = _uiState.value.copy(activeShoppingCount = items.size)
            }
        }

        viewModelScope.launch {
            expenseRepository.getThisMonthExpenses().collect { expenses ->
                val total = expenses.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(monthlyExpenseTotal = total)
            }
        }

        viewModelScope.launch {
            familyRepository.allContacts.collect { contacts ->
                _uiState.value = _uiState.value.copy(familyContacts = contacts)
            }
        }
    }

    fun openVoiceAssistant() {
        _uiState.value = _uiState.value.copy(isVoiceSheetVisible = true)
    }

    fun closeVoiceAssistant() {
        _uiState.value = _uiState.value.copy(
            isVoiceSheetVisible = false,
            pendingConfirmationIntent = null,
            confirmationPrompt = null
        )
    }

    fun executeVoiceCommand(commandText: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            val intent = VoiceCommandParser.parse(commandText)
            handleIntent(intent, onNavigate)
        }
    }

    suspend fun handleIntent(intent: VoiceCommandIntent, onNavigate: (String) -> Unit) {
        when (intent) {
            is VoiceCommandIntent.AddShoppingItem -> {
                shoppingRepository.insertItem(
                    ShoppingItem(
                        title = intent.title,
                        quantity = intent.quantity,
                        unit = intent.unit,
                        category = intent.category,
                        isCompleted = false
                    )
                )
                val msg = if (intent.unit.isNotEmpty()) {
                    "${intent.quantity} ${intent.unit} of ${intent.title} added to your shopping list."
                } else {
                    "${intent.title} added to your shopping list."
                }
                _feedbackEvents.emit(msg)
                closeVoiceAssistant()
            }
            is VoiceCommandIntent.CreateReminder -> {
                val cal = Calendar.getInstance()
                if (!intent.isToday) {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
                taskRepository.insertTask(
                    TaskItem(
                        title = intent.title,
                        dueDateMillis = cal.timeInMillis,
                        timeLabel = intent.timeLabel,
                        isCompleted = false
                    )
                )
                _feedbackEvents.emit("Reminder created: ${intent.title} at ${intent.timeLabel}.")
                closeVoiceAssistant()
            }
            is VoiceCommandIntent.AddExpense -> {
                expenseRepository.insertExpense(
                    ExpenseItem(
                        amount = intent.amount,
                        category = intent.category,
                        description = intent.description
                    )
                )
                _feedbackEvents.emit("₹${intent.amount.toInt()} saved for ${intent.category}.")
                closeVoiceAssistant()
            }
            is VoiceCommandIntent.ViewShoppingList -> {
                closeVoiceAssistant()
                onNavigate("shopping")
            }
            is VoiceCommandIntent.ViewSchedule -> {
                closeVoiceAssistant()
                onNavigate("tasks")
            }
            is VoiceCommandIntent.CallContact -> {
                // Safety confirmation required for phone calls
                _uiState.value = _uiState.value.copy(
                    pendingConfirmationIntent = intent,
                    confirmationPrompt = "Call ${intent.contactName}?"
                )
            }
            is VoiceCommandIntent.CreateNote -> {
                noteRepository.insertNote(
                    NoteItem(
                        title = intent.title,
                        content = intent.content
                    )
                )
                _feedbackEvents.emit("Note saved.")
                closeVoiceAssistant()
            }
            is VoiceCommandIntent.Unknown -> {
                _feedbackEvents.emit("I didn't quite catch that. Try 'Add milk' or 'Remind me to call Dad at 6'.")
            }
        }
    }

    fun confirmPendingAction(onCall: (String) -> Unit) {
        val intent = _uiState.value.pendingConfirmationIntent
        if (intent is VoiceCommandIntent.CallContact) {
            val contact = _uiState.value.familyContacts.firstOrNull {
                it.name.equals(intent.contactName, ignoreCase = true)
            }
            val phone = contact?.phoneNumber ?: ""
            closeVoiceAssistant()
            onCall(phone)
        }
    }

    fun dismissConfirmation() {
        _uiState.value = _uiState.value.copy(
            pendingConfirmationIntent = null,
            confirmationPrompt = null
        )
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = !task.isCompleted))
            val message = if (!task.isCompleted) "Done ✓" else "Task reopened"
            _feedbackEvents.emit(message)
        }
    }

    companion object {
        fun provideFactory(
            userRepository: UserRepository,
            taskRepository: TaskRepository,
            shoppingRepository: ShoppingRepository,
            expenseRepository: ExpenseRepository,
            familyRepository: FamilyRepository,
            noteRepository: NoteRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    userRepository,
                    taskRepository,
                    shoppingRepository,
                    expenseRepository,
                    familyRepository,
                    noteRepository
                ) as T
            }
        }
    }
}
