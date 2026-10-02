package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_contacts")
data class FamilyContact(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String, // e.g., "Son", "Husband", "Daughter", "Brother"
    val phoneNumber: String,
    val avatarColorHex: String = "#5B5BD6",
    val orderPriority: Int = 0
)
