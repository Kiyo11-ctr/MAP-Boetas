package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.local.VehicleDao
import com.valentinesgarage.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * VehicleRepository — the single point of contact between the domain/UI layers
 * and the Vehicle + ServiceTask data sources.
 *
 * Following the repository pattern from the course: the UI never talks directly
 * to DAOs. This allows swapping Room for a remote API without touching ViewModels.
 */
@Singleton
/**
 * Repository for managing vehicle and service task data.
 *
 * This class serves as the single source of truth for all vehicle-related data operations,
 * abstracting the Room DAOs from the UI layer. It provides Flows for real-time UI updates
 * as taught in the MAP711S three-layer architecture.
 */
class VehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val serviceTaskDao: ServiceTaskDao
) {

    /** All vehicles ordered by check-in time (newest first). */
    val allVehiclesWithTasks: Flow<List<VehicleWithTasks>> =
        vehicleDao.getAllVehiclesWithTasks()

    val todayCheckinCount: Flow<Int> = vehicleDao.getTodayCheckinCount()

    fun getVehicleWithTasks(vehicleId: Int): Flow<VehicleWithTasks?> =
        vehicleDao.getVehicleWithTasks(vehicleId)

    /**
     * Registers a vehicle at check-in and creates the standard service checklist.
     * The odometer and condition are permanently recorded here — they cannot be
     * edited later, preventing misuse.
     */
    suspend fun checkInVehicle(
        registrationNumber: String,
        makeAndModel: String,
        driverName: String,
        odometerAtCheckin: Int,
        conditionAtCheckin: VehicleCondition,
        conditionNotes: String,
        assignedMechanicId: Int,
        priority: ServicePriority
    ): Long {
        val vehicle = Vehicle(
            registrationNumber = registrationNumber,
            makeAndModel = makeAndModel,
            driverName = driverName,
            odometerAtCheckin = odometerAtCheckin,
            conditionAtCheckin = conditionAtCheckin,
            conditionNotes = conditionNotes,
            assignedMechanicId = assignedMechanicId,
            priority = priority,
            checkinDateTime = LocalDateTime.now().toString()
        )
        val vehicleId = vehicleDao.insertVehicle(vehicle).toInt()

        // Auto-create a standard checklist for every checked-in vehicle
        val defaultTasks = buildDefaultChecklist(vehicleId, conditionAtCheckin)
        serviceTaskDao.insertTasks(defaultTasks)
        return vehicleId.toLong()
    }

    /** Mechanic marks a task complete with their ID and notes. */
    suspend fun completeTask(task: ServiceTask, mechanicId: Int, notes: String) {
        serviceTaskDao.updateTask(
            task.copy(
                isCompleted = true,
                completedByMechanicId = mechanicId,
                mechanicNotes = notes,
                completedAt = LocalDateTime.now().toString()
            )
        )
    }

    /** Mechanic un-ticks a previously completed task (for corrections). */
    suspend fun uncompleteTask(task: ServiceTask) {
        serviceTaskDao.updateTask(
            task.copy(
                isCompleted = false,
                completedByMechanicId = null,
                mechanicNotes = "",
                completedAt = null
            )
        )
    }

    suspend fun updateVehicleStatus(vehicle: Vehicle, status: VehicleStatus) {
        vehicleDao.updateVehicle(vehicle.copy(status = status))
    }

    /** Adds a custom task not in the default checklist. */
    suspend fun addCustomTask(vehicleId: Int, description: String, category: TaskCategory) {
        serviceTaskDao.insertTask(
            ServiceTask(vehicleId = vehicleId, taskDescription = description, category = category)
        )
    }

    /**
     * Builds a standard checklist based on vehicle condition.
     * Critical vehicles get additional diagnostic tasks.
     */
    private fun buildDefaultChecklist(vehicleId: Int, condition: VehicleCondition): List<ServiceTask> {
        val base = listOf(
            ServiceTask(vehicleId = vehicleId, taskDescription = "Engine oil & filter change", category = TaskCategory.ENGINE_POWERTRAIN),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Air filter inspection / replacement", category = TaskCategory.FILTERS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Fuel filter replacement", category = TaskCategory.FILTERS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check & top up all fluids (coolant, brake, power steering)", category = TaskCategory.FLUIDS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Inspect brake pads & discs (all axles)", category = TaskCategory.BRAKES),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check tyre pressure & tread depth", category = TaskCategory.TYRES_WHEELS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Lubricate drive shaft & suspension points", category = TaskCategory.ENGINE_POWERTRAIN),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check lights (headlights, indicators, brake lights)", category = TaskCategory.ELECTRICAL),
        )
        val extra = when (condition) {
            VehicleCondition.CRITICAL -> listOf(
                ServiceTask(vehicleId = vehicleId, taskDescription = "Engine diagnostic scan (OBD-II)", category = TaskCategory.DIAGNOSTICS),
                ServiceTask(vehicleId = vehicleId, taskDescription = "Inspect fuel injectors", category = TaskCategory.ENGINE_POWERTRAIN),
                ServiceTask(vehicleId = vehicleId, taskDescription = "Check timing chain / belt tension", category = TaskCategory.ENGINE_POWERTRAIN),
            )
            VehicleCondition.FAIR -> listOf(
                ServiceTask(vehicleId = vehicleId, taskDescription = "Engine diagnostic scan (OBD-II)", category = TaskCategory.DIAGNOSTICS),
            )
            VehicleCondition.GOOD -> emptyList()
        }
        return base + extra
    }
}
