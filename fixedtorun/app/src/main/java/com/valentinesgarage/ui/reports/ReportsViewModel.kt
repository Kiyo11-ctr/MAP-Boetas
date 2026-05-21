package com.valentinesgarage.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.EmployeeReport
import com.valentinesgarage.data.model.VehicleWithTasks
import com.valentinesgarage.data.repository.EmployeeRepository
import com.valentinesgarage.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val employeeReports: List<EmployeeReport> = emptyList(),
    val vehicleLog: List<VehicleWithTasks> = emptyList(),
    val filteredVehicleLog: List<VehicleWithTasks> = emptyList(),
    val vehicleSearchQuery: String = "",
    val isLoading: Boolean = true
)

/**
 * ViewModel for the Reports screen — visible to Valentine (manager) only.
 * Combines employee task data and vehicle condition logs into one state object.
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                employeeRepository.getEmployeeReports(),
                vehicleRepository.allVehiclesWithTasks,
                _uiState.map { it.vehicleSearchQuery }.distinctUntilChanged()
            ) { reports, vehicles, query ->
                val filtered = if (query.isBlank()) {
                    vehicles
                } else {
                    vehicles.filter {
                        it.vehicle.registrationNumber.contains(query, ignoreCase = true) ||
                                it.vehicle.makeAndModel.contains(query, ignoreCase = true)
                    }
                }
                Triple(reports, vehicles, filtered)
            }.collect { (reports, vehicles, filtered) ->
                _uiState.update { it.copy(
                    employeeReports = reports,
                    vehicleLog = vehicles,
                    filteredVehicleLog = filtered,
                    isLoading = false
                ) }
            }
        }
    }

    fun onVehicleSearchChanged(query: String) {
        _uiState.update { it.copy(vehicleSearchQuery = query) }
    }
}
