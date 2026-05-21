package com.valentinesgarage.ui.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class EmployeesUiState(
    val employees : List<Employee> = emptyList(),
    val isLoading : Boolean = true
)

@HiltViewModel
class EmployeesViewModel @Inject constructor(repo: EmployeeRepository) : ViewModel() {
    val state: StateFlow<EmployeesUiState> =
        repo.allEmployees
            .map { EmployeesUiState(it, false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EmployeesUiState())
}
