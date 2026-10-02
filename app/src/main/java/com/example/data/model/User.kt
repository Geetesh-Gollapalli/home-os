package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val birthday: String = "10-01",
    val notificationsEnabled: Boolean = true,
    val isDarkMode: Boolean = false,
    val voiceLanguage: String = "en-IN",
    val hasCompletedOnboarding: Boolean = false
)

typealias UserProfile = User
