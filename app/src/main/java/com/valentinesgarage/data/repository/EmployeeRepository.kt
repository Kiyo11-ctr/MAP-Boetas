package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.EmployeeDao
import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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

    /**
     * Builds a report list showing each mechanic's task completion for today.
     * Used on the Reports screen — the key accountability feature for Valentine.
     */
    fun getEmployeeReports(): Flow<List<EmployeeReport>> =
        employeeDao.getActiveEmployees().combine(
            // We use a simple combination here; in production this would be
            // a Room join query for better efficiency.
            employeeDao.getActiveEmployees()
        ) { employees, _ ->
            employees.map { emp ->
                val tasks = serviceTaskDao.getTasksByMechanic(emp.id).first()
                EmployeeReport(
                    employee = emp,
                    tasksCompleted = tasks.count { it.isCompleted },
                    totalTasksAssigned = tasks.size,
                    vehiclesWorkedOn = tasks.map { it.vehicleId }.distinct().size
                )
            }
        }
}
