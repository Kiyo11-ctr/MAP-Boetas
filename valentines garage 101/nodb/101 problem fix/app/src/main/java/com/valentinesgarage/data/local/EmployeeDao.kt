package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.Employee
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(employees: List<Employee>)

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY role ASC, fullName ASC")
    fun getActiveEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees ORDER BY role ASC, fullName ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getById(id: Int): Employee?

    @Query("SELECT COUNT(*) FROM employees")
    suspend fun count(): Int
}
