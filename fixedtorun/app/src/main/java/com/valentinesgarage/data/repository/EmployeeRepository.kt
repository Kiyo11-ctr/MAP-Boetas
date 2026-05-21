package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.EmployeeDao
import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeReport
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * EmployeeRepository — manages mechanic data and assembles per-employee reports.
 * Valentine can see exactly what each mechanic did, on which vehicles.
 */
@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val serviceTaskDao: ServiceTaskDao
) {
    val activeEmployees: Flow<List<Employee>> = employeeDao.getActiveEmployees()
    val allEmployees: Flow<List<Employee>> = employeeDao.getAllEmployees()

    suspend fun getEmployeeById(id: Int): Employee? = employeeDao.getEmployeeById(id)

    suspend fun addEmployee(employee: Employee) = employeeDao.insertEmployee(employee)

    suspend fun deleteEmployee(employee: Employee) = employeeDao.deleteEmployee(employee)

    /**
     * Builds a report list showing each mechanic's task completion for today.
     * Used on the Reports screen — the key accountability feature for Valentine.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getEmployeeReports(): Flow<List<EmployeeReport>> =
        employeeDao.getActiveEmployees().flatMapLatest { employees ->
            if (employees.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val flows = employees.map { emp ->
                serviceTaskDao.getTasksByMechanic(emp.id).map { tasks ->
                    EmployeeReport(
                        employee = emp,
                        tasksCompleted = tasks.count { it.isCompleted },
                        totalTasksAssigned = tasks.size, // Currently only shows completed tasks due to DAO query
                        vehiclesWorkedOn = tasks.map { it.vehicleId }.distinct().size
                    )
                }
            }
            combine(flows) { it.toList() }
        }
}
