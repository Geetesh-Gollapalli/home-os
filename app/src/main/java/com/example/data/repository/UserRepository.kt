package com.example.data.repository

import com.example.data.dao.UserDao
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()

    suspend fun updateProfile(profile: UserProfile) = userDao.insertOrUpdate(profile)
}
