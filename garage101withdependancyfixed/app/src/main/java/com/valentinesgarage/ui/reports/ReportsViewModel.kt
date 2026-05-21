package com.valentinesgarage.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.EmployeeReport
import com.valentinesgarage.data.model.VehicleWithTasks
import com.valentinesgarage.data.repository.EmployeeRepository
import com.valentinesgarage.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ReportsUiState(
    val employeeReports : List<EmployeeReport>  = emptyList(),
    val vehicleLog      : List<VehicleWithTasks> = emptyList(),
    val isLoading       : Boolean = true
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    vehicleRepo  : VehicleRepository,
    employeeRepo : EmployeeRepository
) : ViewModel() {

    val state: StateFlow<ReportsUiState> =
        combine(
            employeeRepo.getEmployeeReports(),
            vehicleRepo.allVehiclesWithTasks
        ) { reports, vehicles ->
            ReportsUiState(
                employeeReports = reports,
                vehicleLog      = vehicles,
                isLoading       = false
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ReportsUiState()
        )
}
