package com.example.data.repository

import com.example.data.dao.TaskDao
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskItem>> = taskDao.getPendingTasks()

    fun getTodayTasks(): Flow<List<TaskItem>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfDay = cal.timeInMillis

        return taskDao.getTodayTasks(startOfDay, endOfDay)
    }

    fun searchTasks(query: String): Flow<List<TaskItem>> = taskDao.searchTasks(query)

    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

    /**
     * Automatically removes completed tasks older than 7 days (604,800,000 ms)
     */
    suspend fun autoCleanupOldCompletedTasks() {
        val sevenDaysAgo = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000)
        taskDao.deleteCompletedTasksOlderThan(sevenDaysAgo)
    }

    suspend fun clearAll() = taskDao.clearAll()
}
