package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ShoppingItem
import com.example.data.repository.ShoppingRepository
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

data class ShoppingUiState(
    val activeItems: List<ShoppingItem> = emptyList(),
    val completedItems: List<ShoppingItem> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val isAddDialogOpen: Boolean = false
)

class ShoppingViewModel(
    private val repository: ShoppingRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _searchQuery = MutableStateFlow("")
    private val _isAddDialogOpen = MutableStateFlow(false)

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.autoCleanupOldCompletedItems()
        }
    }

    val uiState: StateFlow<ShoppingUiState> = combine(
        repository.allItems,
        _selectedCategory,
        _searchQuery,
        _isAddDialogOpen
    ) { allItems, category, query, isDialogOpen ->
        val filtered = allItems.filter { item ->
            val matchesCategory = (category == "All" || item.category.equals(category, ignoreCase = true))
            val matchesQuery = (query.isBlank() || item.title.contains(query, ignoreCase = true))
            matchesCategory && matchesQuery
        }

        val active = filtered.filter { !it.isCompleted }
        val completed = filtered.filter { it.isCompleted }

        ShoppingUiState(
            activeItems = active,
            completedItems = completed,
            selectedCategory = category,
            searchQuery = query,
            isAddDialogOpen = isDialogOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShoppingUiState()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun addItem(title: String, quantity: String, unit: String, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertItem(
                ShoppingItem(
                    title = title.trim(),
                    quantity = if (quantity.isBlank()) "1" else quantity.trim(),
                    unit = unit.trim(),
                    category = category,
                    isCompleted = false
                )
            )
            _feedbackEvents.emit("$title added ✓")
            closeAddDialog()
        }
    }

    fun toggleItem(item: ShoppingItem) {
        viewModelScope.launch {
            val isNowCompleted = !item.isCompleted
            val updated = item.copy(
                isCompleted = isNowCompleted,
                completedAtMillis = if (isNowCompleted) System.currentTimeMillis() else null
            )
            repository.updateItem(updated)
            repository.autoCleanupOldCompletedItems()
            if (updated.isCompleted) {
                _feedbackEvents.emit("${item.title} done ✓")
            }
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
            _feedbackEvents.emit("Item removed")
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.deleteCompletedItems()
            _feedbackEvents.emit("Completed items cleared")
        }
    }

    companion object {
        fun provideFactory(repository: ShoppingRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ShoppingViewModel(repository) as T
                }
            }
    }
}
