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

data class CheckInUiState(
    val registration : String            = "",
    val makeModel    : String            = "",
    val driverName   : String            = "",
    val odometerKm   : String            = "",
    val condition    : VehicleCondition? = null,
    val condNotes    : String            = "",
    val mechanicId   : Int?              = null,
    val priority     : ServicePriority   = ServicePriority.NORMAL,
    val mechanics    : List<Employee>    = emptyList(),
    val isLoading    : Boolean           = false,
    val success      : String?           = null,
    val error        : String?           = null
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val vehicleRepo  : VehicleRepository,
    private val employeeRepo : EmployeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckInUiState())
    val state: StateFlow<CheckInUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            employeeRepo.activeEmployees.collect { list ->
                _state.update { it.copy(
                    mechanics = list.filter { e -> e.role != EmployeeRole.MANAGER }
                )}
            }
        }
    }

    fun onRegChange(v: String)              = _state.update { it.copy(registration = v.uppercase()) }
    fun onMakeChange(v: String)             = _state.update { it.copy(makeModel = v) }
    fun onDriverChange(v: String)           = _state.update { it.copy(driverName = v) }
    fun onOdometerChange(v: String)         = _state.update { it.copy(odometerKm = v) }
    fun onConditionSelect(v: VehicleCondition)  = _state.update { it.copy(condition = v) }
    fun onCondNotesChange(v: String)        = _state.update { it.copy(condNotes = v) }
    fun onMechanicSelect(id: Int)           = _state.update { it.copy(mechanicId = id) }
    fun onPrioritySelect(v: ServicePriority) = _state.update { it.copy(priority = v) }
    fun clearMessages()                     = _state.update { it.copy(success = null, error = null) }

    fun submit() {
        val s  = _state.value
        val km = s.odometerKm.toIntOrNull()
        when {
            s.registration.isBlank() -> { err("Registration number is required."); return }
            s.makeModel.isBlank()    -> { err("Make & model is required."); return }
            km == null || km <= 0    -> { err("Enter a valid odometer reading (km)."); return }
            s.condition == null      -> { err("Select the vehicle condition."); return }
            s.mechanicId == null     -> { err("Assign a lead mechanic."); return }
        }
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                vehicleRepo.checkInVehicle(
                    registrationNumber = s.registration,
                    makeAndModel       = s.makeModel,
                    driverName         = s.driverName,
                    odometerAtCheckin  = km!!,
                    conditionAtCheckin = s.condition!!,
                    conditionNotes     = s.condNotes,
                    assignedMechanicId = s.mechanicId!!,
                    priority           = s.priority
                )
                _state.update {
                    CheckInUiState(
                        mechanics = it.mechanics,
                        success   = "Truck ${s.registration} checked in successfully!"
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed: ${e.message}") }
            }
        }
    }

    private fun err(msg: String) = _state.update { it.copy(error = msg) }
}
