package com.valentinesgarage.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class VehicleWithTasks(
    @Embedded val vehicle: Vehicle,
    @Relation(parentColumn = "id", entityColumn = "vehicleId")
    val tasks: List<ServiceTask>
)
