package com.valentinesgarage.ui.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeRole
import com.valentinesgarage.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmployeesUiState(
    val employees: List<Employee> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class EmployeesViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    val uiState: StateFlow<EmployeesUiState> =
        employeeRepository.allEmployees
            .map { EmployeesUiState(employees = it, isLoading = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EmployeesUiState())

    fun addEmployee(name: String, role: EmployeeRole) {
        viewModelScope.launch {
            employeeRepository.addEmployee(Employee(fullName = name, role = role))
        }
    }

    fun removeEmployee(employee: Employee) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employee)
        }
    }
}
