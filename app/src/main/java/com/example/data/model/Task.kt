package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val dueDateMillis: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val timeLabel: String = "",
    val repeatOption: String = "None",
    val note: String = "",
    val isUrgent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAtMillis: Long? = null
)

typealias TaskItem = Task
