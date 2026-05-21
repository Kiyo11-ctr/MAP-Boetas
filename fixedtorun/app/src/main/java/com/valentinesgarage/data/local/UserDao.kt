package com.valentinesgarage.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.valentinesgarage.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>
}
