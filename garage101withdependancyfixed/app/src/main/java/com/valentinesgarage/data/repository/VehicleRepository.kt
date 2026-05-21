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
    private val vehicleDao     : VehicleDao,
    private val serviceTaskDao : ServiceTaskDao
) {
    val allVehiclesWithTasks: Flow<List<VehicleWithTasks>> = vehicleDao.getAllWithTasks()

    suspend fun checkInVehicle(
        registrationNumber  : String,
        makeAndModel        : String,
        driverName          : String,
        odometerAtCheckin   : Int,
        conditionAtCheckin  : VehicleCondition,
        conditionNotes      : String,
        assignedMechanicId  : Int,
        priority            : ServicePriority
    ) {
        val id = vehicleDao.insert(
            Vehicle(
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
        ).toInt()
        serviceTaskDao.insertAll(buildChecklist(id, conditionAtCheckin))
    }

    suspend fun completeTask(task: ServiceTask, mechanicId: Int, notes: String) {
        serviceTaskDao.update(
            task.copy(
                isCompleted           = true,
                completedByMechanicId = mechanicId,
                mechanicNotes         = notes,
                completedAt           = LocalDateTime.now().toString()
            )
        )
    }

    suspend fun uncompleteTask(task: ServiceTask) {
        serviceTaskDao.update(
            task.copy(
                isCompleted           = false,
                completedByMechanicId = null,
                mechanicNotes         = "",
                completedAt           = null
            )
        )
    }

    suspend fun addCustomTask(vehicleId: Int, description: String, category: TaskCategory) {
        serviceTaskDao.insert(
            ServiceTask(vehicleId = vehicleId, taskDescription = description, category = category)
        )
    }

    suspend fun updateVehicleStatus(vehicle: Vehicle, status: VehicleStatus) {
        vehicleDao.update(vehicle.copy(status = status))
    }

    private fun buildChecklist(vehicleId: Int, condition: VehicleCondition): List<ServiceTask> {
        val base = listOf(
            task(vehicleId, "Engine oil & filter change",             TaskCategory.ENGINE_POWERTRAIN),
            task(vehicleId, "Air filter inspection / replacement",    TaskCategory.FILTERS),
            task(vehicleId, "Fuel filter replacement",               TaskCategory.FILTERS),
            task(vehicleId, "Check & top up all fluids",             TaskCategory.FLUIDS),
            task(vehicleId, "Inspect brake pads & discs",            TaskCategory.BRAKES),
            task(vehicleId, "Check tyre pressure & tread depth",     TaskCategory.TYRES_WHEELS),
            task(vehicleId, "Lubricate drive shaft & suspension",    TaskCategory.ENGINE_POWERTRAIN),
            task(vehicleId, "Check all lights",                      TaskCategory.ELECTRICAL)
        )
        val extra = when (condition) {
            VehicleCondition.CRITICAL -> listOf(
                task(vehicleId, "Engine diagnostic scan (OBD-II)",   TaskCategory.DIAGNOSTICS),
                task(vehicleId, "Inspect fuel injectors",            TaskCategory.ENGINE_POWERTRAIN),
                task(vehicleId, "Check timing chain / belt tension", TaskCategory.ENGINE_POWERTRAIN)
            )
            VehicleCondition.FAIR -> listOf(
                task(vehicleId, "Engine diagnostic scan (OBD-II)",   TaskCategory.DIAGNOSTICS)
            )
            VehicleCondition.GOOD -> emptyList()
        }
        return base + extra
    }

    private fun task(vehicleId: Int, desc: String, cat: TaskCategory) =
        ServiceTask(vehicleId = vehicleId, taskDescription = desc, category = cat)
}
