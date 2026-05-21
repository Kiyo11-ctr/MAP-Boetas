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
    val mechanics: List<com.valentinesgarage.data.model.Employee> = emptyList(),

    // Field-specific errors for professional validation
    val registrationError: String? = null,
    val makeModelError: String? = null,
    val odometerError: String? = null,
    val conditionError: String? = null,
    val mechanicError: String? = null
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

    fun onRegistrationChanged(value: String) = _uiState.update { it.copy(registrationNumber = value.uppercase(), registrationError = null) }
    fun onMakeModelChanged(value: String) = _uiState.update { it.copy(makeAndModel = value, makeModelError = null) }
    fun onDriverNameChanged(value: String) = _uiState.update { it.copy(driverName = value) }
    fun onOdometerChanged(value: String) = _uiState.update { it.copy(odometerKm = value, odometerError = null) }
    fun onConditionSelected(value: VehicleCondition) = _uiState.update { it.copy(condition = value, conditionError = null) }
    fun onConditionNotesChanged(value: String) = _uiState.update { it.copy(conditionNotes = value) }
    fun onMechanicSelected(id: Int) = _uiState.update { it.copy(selectedMechanicId = id, mechanicError = null) }
    fun onPrioritySelected(value: ServicePriority) = _uiState.update { it.copy(priority = value) }
    fun clearMessages() = _uiState.update { it.copy(successMessage = null, errorMessage = null) }

    fun submitCheckIn() {
        val state = _uiState.value
        val odometer = state.odometerKm.toIntOrNull()

        var hasError = false

        if (state.registrationNumber.isBlank()) {
            _uiState.update { it.copy(registrationError = "Registration is required") }
            hasError = true
        }
        if (state.makeAndModel.isBlank()) {
            _uiState.update { it.copy(makeModelError = "Make & model is required") }
            hasError = true
        }
        if (odometer == null || odometer <= 0) {
            _uiState.update { it.copy(odometerError = "Enter valid km") }
            hasError = true
        }
        if (state.condition == null) {
            _uiState.update { it.copy(conditionError = "Select condition") }
            hasError = true
        }
        if (state.selectedMechanicId == null) {
            _uiState.update { it.copy(mechanicError = "Assign a mechanic") }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                vehicleRepository.checkInVehicle(
                    registrationNumber = state.registrationNumber,
                    makeAndModel = state.makeAndModel,
                    driverName = state.driverName,
                    odometerAtCheckin = odometer!!,
                    conditionAtCheckin = state.condition!!,
                    conditionNotes = state.conditionNotes,
                    assignedMechanicId = state.selectedMechanicId!!,
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
