package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_items")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val quantity: String = "1",
    val unit: String = "",
    val category: String = "Groceries", // Groceries, Vegetables, Household, Personal, Other
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAtMillis: Long? = null
)
