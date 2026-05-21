package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.EmployeeDao
import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepository @Inject constructor(
    private val employeeDao    : EmployeeDao,
    private val serviceTaskDao : ServiceTaskDao
) {
    val activeEmployees : Flow<List<Employee>> = employeeDao.getActive()
    val allEmployees    : Flow<List<Employee>> = employeeDao.getAll()

    fun getEmployeeReports(): Flow<List<EmployeeReport>> =
        employeeDao.getActive().map { employees ->
            employees.map { emp ->
                val tasks = serviceTaskDao.getByMechanic(emp.id).first()
                EmployeeReport(
                    employee           = emp,
                    tasksCompleted     = tasks.count { it.isCompleted },
                    totalTasksAssigned = tasks.size,
                    vehiclesWorkedOn   = tasks.map { it.vehicleId }.distinct().size
                )
            }
        }
}
