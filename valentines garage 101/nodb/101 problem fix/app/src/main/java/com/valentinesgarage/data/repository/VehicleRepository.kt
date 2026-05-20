package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.local.VehicleDao
import com.valentinesgarage.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val serviceTaskDao: ServiceTaskDao
) {
    val allVehiclesWithTasks: Flow<List<VehicleWithTasks>> =
        vehicleDao.getAllVehiclesWithTasks()

    suspend fun checkInVehicle(
        registrationNumber: String,
        makeAndModel: String,
        driverName: String,
        odometerAtCheckin: Int,
        conditionAtCheckin: VehicleCondition,
        conditionNotes: String,
        assignedMechanicId: Int,
        priority: ServicePriority
    ) {
        val vehicle = Vehicle(
            registrationNumber  = registrationNumber,
            makeAndModel        = makeAndModel,
            driverName          = driverName,
            odometerAtCheckin   = odometerAtCheckin,
            conditionAtCheckin  = conditionAtCheckin,
            conditionNotes      = conditionNotes,
            assignedMechanicId  = assignedMechanicId,
            priority            = priority,
            checkinDateTime     = LocalDateTime.now().toString()
        )
        val vehicleId = vehicleDao.insertVehicle(vehicle).toInt()
        serviceTaskDao.insertTasks(buildChecklist(vehicleId, conditionAtCheckin))
    }

    suspend fun completeTask(task: ServiceTask, mechanicId: Int, notes: String) {
        serviceTaskDao.updateTask(
            task.copy(
                isCompleted           = true,
                completedByMechanicId = mechanicId,
                mechanicNotes         = notes,
                completedAt           = LocalDateTime.now().toString()
            )
        )
    }

    suspend fun uncompleteTask(task: ServiceTask) {
        serviceTaskDao.updateTask(
            task.copy(isCompleted = false, completedByMechanicId = null,
                      mechanicNotes = "", completedAt = null)
        )
    }

    suspend fun addCustomTask(vehicleId: Int, description: String, category: TaskCategory) {
        serviceTaskDao.insertTask(
            ServiceTask(vehicleId = vehicleId, taskDescription = description, category = category)
        )
    }

    suspend fun updateVehicleStatus(vehicle: Vehicle, status: VehicleStatus) {
        vehicleDao.updateVehicle(vehicle.copy(status = status))
    }

    private fun buildChecklist(vehicleId: Int, condition: VehicleCondition): List<ServiceTask> {
        val base = listOf(
            ServiceTask(vehicleId = vehicleId, taskDescription = "Engine oil & filter change",                    category = TaskCategory.ENGINE_POWERTRAIN),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Air filter inspection / replacement",           category = TaskCategory.FILTERS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Fuel filter replacement",                      category = TaskCategory.FILTERS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check & top up all fluids",                    category = TaskCategory.FLUIDS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Inspect brake pads & discs (all axles)",       category = TaskCategory.BRAKES),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check tyre pressure & tread depth",            category = TaskCategory.TYRES_WHEELS),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Lubricate drive shaft & suspension",           category = TaskCategory.ENGINE_POWERTRAIN),
            ServiceTask(vehicleId = vehicleId, taskDescription = "Check all lights (headlights, indicators)",    category = TaskCategory.ELECTRICAL),
        )
        val extra = when (condition) {
            VehicleCondition.CRITICAL -> listOf(
                ServiceTask(vehicleId = vehicleId, taskDescription = "Engine diagnostic scan (OBD-II)",  category = TaskCategory.DIAGNOSTICS),
                ServiceTask(vehicleId = vehicleId, taskDescription = "Inspect fuel injectors",           category = TaskCategory.ENGINE_POWERTRAIN),
                ServiceTask(vehicleId = vehicleId, taskDescription = "Check timing chain / belt",        category = TaskCategory.ENGINE_POWERTRAIN),
            )
            VehicleCondition.FAIR -> listOf(
                ServiceTask(vehicleId = vehicleId, taskDescription = "Engine diagnostic scan (OBD-II)", category = TaskCategory.DIAGNOSTICS),
            )
            VehicleCondition.GOOD -> emptyList()
        }
        return base + extra
    }
}
