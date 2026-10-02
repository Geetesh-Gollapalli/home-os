package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FamilyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_contacts ORDER BY orderPriority ASC, id ASC")
    fun getAllContacts(): Flow<List<FamilyContact>>

    @Query("SELECT * FROM family_contacts ORDER BY orderPriority ASC, id ASC LIMIT 2")
    fun getAllContactsSync(): List<FamilyContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: FamilyContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<FamilyContact>)

    @Update
    suspend fun updateContact(contact: FamilyContact)

    @Delete
    suspend fun deleteContact(contact: FamilyContact)

    @Query("DELETE FROM family_contacts")
    suspend fun clearAll()
}
