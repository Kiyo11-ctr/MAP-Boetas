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
    val mechanics:         List<Employee> = emptyList(),
    val isLoading:         Boolean = true,
    val toast:             String? = null
)

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val vehicleRepo:  VehicleRepository,
    private val employeeRepo: EmployeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceUiState())
    val state: StateFlow<ServiceUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(vehicleRepo.allVehiclesWithTasks, employeeRepo.activeEmployees) { vehicles, emps ->
                ServiceUiState(
                    vehiclesWithTasks = vehicles.filter { it.vehicle.status != VehicleStatus.COMPLETED },
                    mechanics         = emps.filter { it.role != EmployeeRole.MANAGER },
                    isLoading         = false
                )
            }.collect { _state.value = it }
        }
    }

    fun completeTask(task: ServiceTask, mechanic: Employee, notes: String) {
        viewModelScope.launch {
            vehicleRepo.completeTask(task, mechanic.id, notes)
            _state.update { it.copy(toast = "Task completed by ${mechanic.fullName}") }
        }
    }

    fun uncompleteTask(task: ServiceTask) = viewModelScope.launch { vehicleRepo.uncompleteTask(task) }

    fun addCustomTask(vehicleId: Int, desc: String, cat: TaskCategory) =
        viewModelScope.launch { vehicleRepo.addCustomTask(vehicleId, desc, cat) }

    fun markVehicleComplete(vt: VehicleWithTasks) {
        if (!vt.tasks.all { it.isCompleted }) {
            _state.update { it.copy(toast = "All tasks must be ticked off first.") }
            return
        }
        viewModelScope.launch {
            vehicleRepo.updateVehicleStatus(vt.vehicle, VehicleStatus.COMPLETED)
            _state.update { it.copy(toast = "${vt.vehicle.registrationNumber} marked complete.") }
        }
    }

    fun clearToast() = _state.update { it.copy(toast = null) }
}
