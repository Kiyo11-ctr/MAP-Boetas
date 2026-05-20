package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for Authentication.
 * Stores login credentials and role-based permissions.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val role: UserRole = UserRole.MECHANIC,
    val employeeId: Int? = null 
)

enum class UserRole { ADMIN, RECEPTIONIST, MECHANIC }
