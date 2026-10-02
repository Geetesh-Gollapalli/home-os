package com.example.data.repository

import com.example.data.dao.ShoppingDao
import com.example.data.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val shoppingDao: ShoppingDao) {
    val allItems: Flow<List<ShoppingItem>> = shoppingDao.getAllItems()
    val activeItems: Flow<List<ShoppingItem>> = shoppingDao.getActiveItems()

    fun searchItems(query: String): Flow<List<ShoppingItem>> = shoppingDao.searchItems(query)

    suspend fun insertItem(item: ShoppingItem): Long = shoppingDao.insertItem(item)

    suspend fun updateItem(item: ShoppingItem) = shoppingDao.updateItem(item)

    suspend fun deleteItem(item: ShoppingItem) = shoppingDao.deleteItem(item)

    suspend fun deleteCompletedItems() = shoppingDao.deleteCompletedItems()

    /**
     * Automatically removes completed shopping items checked off more than 24 hours ago (86,400,000 ms)
     */
    suspend fun autoCleanupOldCompletedItems() {
        val twentyFourHoursAgo = System.currentTimeMillis() - (24L * 60 * 60 * 1000)
        shoppingDao.deleteCompletedShoppingItemsOlderThan(twentyFourHoursAgo)
    }

    suspend fun clearAll() = shoppingDao.clearAll()
}
