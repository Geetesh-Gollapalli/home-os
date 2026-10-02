package com.example.data.repository

import com.example.data.dao.FamilyDao
import com.example.data.model.FamilyContact
import kotlinx.coroutines.flow.Flow

class FamilyRepository(private val familyDao: FamilyDao) {
    val allContacts: Flow<List<FamilyContact>> = familyDao.getAllContacts()

    suspend fun insertContact(contact: FamilyContact): Long = familyDao.insertContact(contact)

    suspend fun updateContact(contact: FamilyContact) = familyDao.updateContact(contact)

    suspend fun deleteContact(contact: FamilyContact) = familyDao.deleteContact(contact)

    suspend fun clearAll() = familyDao.clearAll()
}
