package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User entity for Authentication.
 * Stores login credentials and links to an optional Employee profile.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val passwordHash: String,
    val employeeId: Int? = null // Link to the Employee entity if applicable
)
