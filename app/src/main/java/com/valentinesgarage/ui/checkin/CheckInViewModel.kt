package com.valentinesgarage.ui.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valentinesgarage.data.model.*
import com.valentinesgarage.data.repository.EmployeeRepository
import com.valentinesgarage.data.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the Check-In screen. */
data class CheckInUiState(
    val registrationNumber: String = "",
    val makeAndModel: String = "",
    val driverName: String = "",
    val odometerKm: String = "",
    val condition: VehicleCondition? = null,
    val conditionNotes: String = "",
    val selectedMechanicId: Int? = null,
    val priority: ServicePriority = ServicePriority.NORMAL,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val mechanics: List<com.valentinesgarage.data.model.Employee> = emptyList()
)

/**
 * ViewModel for the Truck Check-In screen.
 *
 * Holds form state and validates input before calling the repository.
 * Exposes [uiState] as StateFlow so the Composable automatically recomposes
 * on state changes — pure Unidirectional Data Flow (UDF).
 */
@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    init {
        // Load mechanic list for the assignment dropdown
        viewModelScope.launch {
            employeeRepository.activeEmployees
                .filter { it.isNotEmpty() }
                .collect { employees ->
                    _uiState.update { it.copy(mechanics = employees.filter { e ->
                        e.role != EmployeeRole.MANAGER
                    }) }
                }
        }
    }

    fun onRegistrationChanged(value: String) = _uiState.update { it.copy(registrationNumber = value.uppercase()) }
    fun onMakeModelChanged(value: String) = _uiState.update { it.copy(makeAndModel = value) }
    fun onDriverNameChanged(value: String) = _uiState.update { it.copy(driverName = value) }
    fun onOdometerChanged(value: String) = _uiState.update { it.copy(odometerKm = value) }
    fun onConditionSelected(value: VehicleCondition) = _uiState.update { it.copy(condition = value) }
    fun onConditionNotesChanged(value: String) = _uiState.update { it.copy(conditionNotes = value) }
    fun onMechanicSelected(id: Int) = _uiState.update { it.copy(selectedMechanicId = id) }
    fun onPrioritySelected(value: ServicePriority) = _uiState.update { it.copy(priority = value) }
    fun clearMessages() = _uiState.update { it.copy(successMessage = null, errorMessage = null) }

    /**
     * Validates and submits the check-in form.
     * Odometer and condition are recorded here permanently.
     */
    fun submitCheckIn() {
        val state = _uiState.value
        val odometer = state.odometerKm.toIntOrNull()

        if (state.registrationNumber.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Registration number is required.") }
            return
        }
        if (state.makeAndModel.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Make & model is required.") }
            return
        }
        if (odometer == null || odometer <= 0) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid odometer reading.") }
            return
        }
        if (state.condition == null) {
            _uiState.update { it.copy(errorMessage = "Please select the vehicle condition.") }
            return
        }
        if (state.selectedMechanicId == null) {
            _uiState.update { it.copy(errorMessage = "Please assign a lead mechanic.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                vehicleRepository.checkInVehicle(
                    registrationNumber = state.registrationNumber,
                    makeAndModel = state.makeAndModel,
                    driverName = state.driverName,
                    odometerAtCheckin = odometer,
                    conditionAtCheckin = state.condition,
                    conditionNotes = state.conditionNotes,
                    assignedMechanicId = state.selectedMechanicId,
                    priority = state.priority
                )
                // Reset form on success
                _uiState.update {
                    CheckInUiState(
                        mechanics = it.mechanics,
                        successMessage = "Truck ${state.registrationNumber} checked in successfully!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Check-in failed: ${e.message}") }
            }
        }
    }
}
