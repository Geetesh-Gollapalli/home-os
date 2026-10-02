package com.example.data.repository

import com.example.data.dao.ExpenseDao
import com.example.data.model.ExpenseItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<ExpenseItem>> = expenseDao.getAllExpenses()

    fun getThisMonthExpenses(): Flow<List<ExpenseItem>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return expenseDao.getExpensesSince(cal.timeInMillis)
    }

    fun searchExpenses(query: String): Flow<List<ExpenseItem>> = expenseDao.searchExpenses(query)

    suspend fun insertExpense(expense: ExpenseItem): Long = expenseDao.insertExpense(expense)

    suspend fun deleteExpense(expense: ExpenseItem) = expenseDao.deleteExpense(expense)

    suspend fun clearAll() = expenseDao.clearAll()
}
