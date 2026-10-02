package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ExpenseItem
import com.example.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CategoryBreakdown(
    val category: String,
    val total: Double,
    val percentage: Float
)

data class ExpenseUiState(
    val totalThisMonth: Double = 0.0,
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val recentExpenses: List<ExpenseItem> = emptyList(),
    val selectedCategoryFilter: String? = null,
    val isAddDialogOpen: Boolean = false
)

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _isAddDialogOpen = MutableStateFlow(false)
    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)

    private val _feedbackEvents = MutableSharedFlow<String>()
    val feedbackEvents: SharedFlow<String> = _feedbackEvents.asSharedFlow()

    val uiState: StateFlow<ExpenseUiState> = combine(
        repository.allExpenses,
        _isAddDialogOpen,
        _selectedCategoryFilter
    ) { expenses, isDialogOpen, categoryFilter ->
        val total = expenses.sumOf { it.amount }
        val categoryTotals = expenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val breakdowns = categoryTotals.map { (cat, sum) ->
            CategoryBreakdown(
                category = cat,
                total = sum,
                percentage = if (total > 0) (sum / total).toFloat() else 0f
            )
        }.sortedByDescending { it.total }

        val filteredExpenses = if (categoryFilter != null) {
            expenses.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        } else {
            expenses
        }

        ExpenseUiState(
            totalThisMonth = total,
            categoryBreakdown = breakdowns,
            recentExpenses = filteredExpenses,
            selectedCategoryFilter = categoryFilter,
            isAddDialogOpen = isDialogOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseUiState()
    )

    fun openAddDialog() {
        _isAddDialogOpen.value = true
    }

    fun closeAddDialog() {
        _isAddDialogOpen.value = false
    }

    fun selectCategoryFilter(category: String?) {
        if (_selectedCategoryFilter.value == category) {
            _selectedCategoryFilter.value = null
        } else {
            _selectedCategoryFilter.value = category
        }
    }

    fun addExpense(amountStr: String, category: String, description: String) {
        val amount = amountStr.toDoubleOrNull() ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            repository.insertExpense(
                ExpenseItem(
                    amount = amount,
                    category = category,
                    description = description.trim(),
                    timestamp = System.currentTimeMillis()
                )
            )
            _feedbackEvents.emit("Expense saved")
            closeAddDialog()
        }
    }

    fun deleteExpense(expense: ExpenseItem) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _feedbackEvents.emit("Expense deleted")
        }
    }

    companion object {
        fun provideFactory(repository: ExpenseRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExpenseViewModel(repository) as T
                }
            }
    }
}
