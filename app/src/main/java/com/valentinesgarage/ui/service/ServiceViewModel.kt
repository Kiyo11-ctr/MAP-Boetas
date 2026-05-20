package com.valentinesgarage.ui.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.*
import com.valentinesgarage.data.repository.EmployeeRepository
import com.valentinesgarage.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceUiState(
    val vehiclesWithTasks: List<VehicleWithTasks> = emptyList(),
    val filteredVehicles: List<VehicleWithTasks> = emptyList(),
    val mechanics: List<Employee> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val toastMessage: String? = null
)

/**
 * ViewModel for the collaborative Service Board screen.
 *
 * Mechanics see all active vehicles and their checklists. Ticking a task
 * records which mechanic did it and when — the core anti-duplication feature.
 */
/**
 * ViewModel for the Service Board screen.
 *
 * Manages the state of active vehicles and their tasks. Following the UDF (Unidirectional Data Flow)
 * pattern, it exposes a UI state Flow and handles events like completing tasks or adding custom ones.
 */
@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceUiState())
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                vehicleRepository.allVehiclesWithTasks,
                employeeRepository.activeEmployees,
                _uiState.map { it.searchQuery }.distinctUntilChanged()
            ) { vehicles, employees, query ->
                val activeVehicles = vehicles.filter { it.vehicle.status != VehicleStatus.COMPLETED }
                val filtered = if (query.isBlank()) {
                    activeVehicles
                } else {
                    activeVehicles.filter {
                        it.vehicle.registrationNumber.contains(query, ignoreCase = true) ||
                                it.vehicle.makeAndModel.contains(query, ignoreCase = true)
                    }
                }
                Triple(activeVehicles, filtered, employees)
            }.collect { (active, filtered, employees) ->
                _uiState.update { it.copy(
                    vehiclesWithTasks = active,
                    filteredVehicles = filtered,
                    mechanics = employees.filter { it.role != EmployeeRole.MANAGER },
                    isLoading = false
                ) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Marks a task complete with the mechanic who did it.
     * This is the core collaborative feature: no task can be "done" without
     * attributing it to a specific mechanic and recording notes.
     */
    fun completeTask(task: ServiceTask, mechanic: Employee, notes: String) {
        viewModelScope.launch {
            vehicleRepository.completeTask(task, mechanic.id, notes)
            _uiState.update { it.copy(toastMessage = "Task completed by ${mechanic.fullName}") }
        }
    }

    fun uncompleteTask(task: ServiceTask) {
        viewModelScope.launch {
            vehicleRepository.uncompleteTask(task)
        }
    }

    fun addCustomTask(vehicleId: Int, description: String, category: TaskCategory) {
        viewModelScope.launch {
            vehicleRepository.addCustomTask(vehicleId, description, category)
        }
    }

    fun markVehicleComplete(vehicleWithTasks: VehicleWithTasks) {
        val allDone = vehicleWithTasks.tasks.all { it.isCompleted }
        if (!allDone) {
            _uiState.update { it.copy(toastMessage = "All tasks must be completed first.") }
            return
        }
        viewModelScope.launch {
            vehicleRepository.updateVehicleStatus(vehicleWithTasks.vehicle, VehicleStatus.COMPLETED)
            _uiState.update { it.copy(toastMessage = "Vehicle ${vehicleWithTasks.vehicle.registrationNumber} marked as complete.") }
        }
    }

    fun clearToast() = _uiState.update { it.copy(toastMessage = null) }
}
