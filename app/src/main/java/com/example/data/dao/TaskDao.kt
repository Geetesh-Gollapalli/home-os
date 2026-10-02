package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateMillis ASC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDateMillis ASC")
    fun getPendingTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDateMillis ASC LIMIT 25")
    fun getPendingTasksSync(): List<Task>

    @Query("SELECT * FROM tasks WHERE dueDateMillis >= :startOfDay AND dueDateMillis <= :endOfDay ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getTodayTasks(startOfDay: Long, endOfDay: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM tasks WHERE isCompleted = 1 AND (completedAtMillis < :thresholdMillis OR (completedAtMillis IS NULL AND createdAt < :thresholdMillis))")
    suspend fun deleteCompletedTasksOlderThan(thresholdMillis: Long)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()
}
