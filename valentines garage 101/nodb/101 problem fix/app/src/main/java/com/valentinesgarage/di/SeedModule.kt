package com.valentinesgarage.di

import com.valentinesgarage.data.local.EmployeeDao
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeRole
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds default employees into the database on first launch.
 * Injected by Hilt and called from GarageApplication.
 */
@Singleton
class DatabaseSeeder @Inject constructor(private val employeeDao: EmployeeDao) {

    fun seedIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            if (employeeDao.count() == 0) {
                employeeDao.insertAll(defaultEmployees)
            }
        }
    }

    private val defaultEmployees = listOf(
        Employee(fullName = "Valentine Mutorwa",  role = EmployeeRole.MANAGER),
        Employee(fullName = "Johannes Kavendjii", role = EmployeeRole.LEAD_MECHANIC),
        Employee(fullName = "Petrus Nangolo",     role = EmployeeRole.SENIOR_MECHANIC),
        Employee(fullName = "Maria Shikongo",     role = EmployeeRole.SENIOR_MECHANIC),
        Employee(fullName = "Festus Haimbodi",    role = EmployeeRole.JUNIOR_MECHANIC),
        Employee(fullName = "Absalom Tjiueza",    role = EmployeeRole.JUNIOR_MECHANIC),
    )
}
