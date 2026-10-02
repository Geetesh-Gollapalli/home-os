package com.example

import android.app.Application
import com.example.data.db.MomOSDatabase
import com.example.data.repository.ExpenseRepository
import com.example.data.repository.FamilyRepository
import com.example.data.repository.NoteRepository
import com.example.data.repository.ShoppingRepository
import com.example.data.repository.TaskRepository
import com.example.data.repository.UserRepository
import com.example.widget.HomeOSTasksShoppingWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MomOSApplication : Application() {
    val database by lazy { MomOSDatabase.getDatabase(this) }

    val shoppingRepository by lazy { ShoppingRepository(database.shoppingDao()) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val familyRepository by lazy { FamilyRepository(database.familyDao()) }
    val noteRepository by lazy { NoteRepository(database.noteDao()) }
    val userRepository by lazy { UserRepository(database.userDao()) }

    override fun onCreate() {
        super.onCreate()
        // Automatic cleanup of old completed items (Tasks > 7 days, Shopping > 24 hrs)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                taskRepository.autoCleanupOldCompletedTasks()
                shoppingRepository.autoCleanupOldCompletedItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Keep Tasks & Shopping Home Screen Widget synchronized with Room database changes
        CoroutineScope(Dispatchers.IO).launch {
            try {
                combine(taskRepository.allTasks, shoppingRepository.allItems) { _, _ -> }
                    .collect {
                        HomeOSTasksShoppingWidgetProvider.notifyDataChanged(this@MomOSApplication)
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
