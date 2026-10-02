package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.TaskItem
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class TaskUiState(
    val todayTasks: List<TaskItem> = emptyList(),
    val upcomingTasks: List<TaskItem> = emptyList(),
    val completedTasks: List<TaskItem> = emptyList(),
    val isAddDialogOpen: Boolean = false
)

class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _isAddDialogOpen = MutableStateFlow(false)

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.autoCleanupOldCompletedTasks()
        }
    }

    val uiState: StateFlow<TaskUiState> = combine(
        repository.allTasks,
        _isAddDialogOpen
    ) { allTasks, isDialogOpen ->
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfToday = cal.timeInMillis

        val today = mutableListOf<TaskItem>()
        val upcoming = mutableListOf<TaskItem>()
        val completed = mutableListOf<TaskItem>()

        for (task in allTasks) {
            if (task.isCompleted) {
                completed.add(task)
            } else if (task.dueDateMillis <= endOfToday) {
                today.add(task)
            } else {
                upcoming.add(task)
            }
        }

        TaskUiState(
            todayTasks = today,
            upcomingTasks = upcoming,
            completedTasks = completed,
            isAddDialogOpen = isDialogOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState()
    )

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun addTask(
        title: String,
        preset: String,
        exactTimeLabel: String,
        repeatOption: String,
        note: String
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val timeLabel = when (preset) {
                "Later today" -> {
                    cal.add(Calendar.HOUR_OF_DAY, 3)
                    if (exactTimeLabel.isNotBlank()) exactTimeLabel else "Later today"
                }
                "This evening" -> {
                    cal.set(Calendar.HOUR_OF_DAY, 18)
                    cal.set(Calendar.MINUTE, 0)
                    if (exactTimeLabel.isNotBlank()) exactTimeLabel else "6:00 PM"
                }
                "Tomorrow" -> {
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    if (exactTimeLabel.isNotBlank()) exactTimeLabel else "Tomorrow"
                }
                "Next week" -> {
                    cal.add(Calendar.DAY_OF_YEAR, 7)
                    if (exactTimeLabel.isNotBlank()) exactTimeLabel else "Next week"
                }
                else -> exactTimeLabel.ifBlank { "Today" }
            }

            repository.insertTask(
                TaskItem(
                    title = title.trim(),
                    dueDateMillis = cal.timeInMillis,
                    timeLabel = timeLabel,
                    repeatOption = repeatOption,
                    note = note.trim(),
                    isCompleted = false
                )
            )
            _feedbackEvents.emit("Reminder saved ✓")
            closeAddDialog()
        }
    }

    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            val isNowCompleted = !task.isCompleted
            val updated = task.copy(
                isCompleted = isNowCompleted,
                completedAtMillis = if (isNowCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
            repository.autoCleanupOldCompletedTasks()
            if (updated.isCompleted) {
                _feedbackEvents.emit("Done ✓")
            }
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _feedbackEvents.emit("Reminder removed")
        }
    }

    companion object {
        fun provideFactory(repository: TaskRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(repository) as T
                }
            }
    }
}
