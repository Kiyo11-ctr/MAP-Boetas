package com.valentinesgarage.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.VehicleWithTasks
import com.valentinesgarage.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DashboardUiState(
    val vehiclesWithTasks: List<VehicleWithTasks> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    vehicleRepository: VehicleRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        vehicleRepository.allVehiclesWithTasks
            .map { DashboardUiState(vehiclesWithTasks = it, isLoading = false) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}
