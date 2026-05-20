package com.valentinesgarage.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Convenience data class used by Room to return a Vehicle together with
 * all its associated ServiceTasks in a single query.
 * This avoids N+1 query problems and simplifies ViewModel logic.
 */
data class VehicleWithTasks(
    @Embedded val vehicle: Vehicle,
    @Relation(
        parentColumn = "id",
        entityColumn = "vehicleId"
    )
    val tasks: List<ServiceTask>
)
