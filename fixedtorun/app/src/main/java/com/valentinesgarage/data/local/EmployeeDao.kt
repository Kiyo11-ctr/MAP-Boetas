package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.Employee
import kotlinx.coroutines.flow.Flow

/** Data Access Object for Employee operations. */
@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployees(employees: List<Employee>)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Query("SELECT * FROM employees WHERE isActive = 1 ORDER BY role ASC, fullName ASC")
    fun getActiveEmployees(): Flow<List<Employee>>

    @Query("SELECT * FROM employees WHERE id = :id")
    suspend fun getEmployeeById(id: Int): Employee?

    @Query("SELECT * FROM employees ORDER BY role ASC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Delete
    suspend fun deleteEmployee(employee: Employee)
}
