package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveItems(): Flow<List<ShoppingItem>>

    @Query("SELECT * FROM shopping_items WHERE isCompleted = 0 ORDER BY createdAt DESC LIMIT 25")
    fun getActiveItemsSync(): List<ShoppingItem>

    @Query("SELECT * FROM shopping_items WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchItems(query: String): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ShoppingItem>)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items WHERE isCompleted = 1")
    suspend fun deleteCompletedItems()

    @Query("DELETE FROM shopping_items WHERE isCompleted = 1 AND (completedAtMillis < :thresholdMillis OR (completedAtMillis IS NULL AND createdAt < :thresholdMillis))")
    suspend fun deleteCompletedShoppingItemsOlderThan(thresholdMillis: Long)

    @Query("DELETE FROM shopping_items")
    suspend fun clearAll()
}
