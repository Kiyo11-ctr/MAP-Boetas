package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName : String,
    val role     : EmployeeRole,
    val isActive : Boolean = true
)

enum class EmployeeRole { MANAGER, LEAD_MECHANIC, SENIOR_MECHANIC, JUNIOR_MECHANIC }
