package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_tasks",
    foreignKeys = [ForeignKey(
        entity        = Vehicle::class,
        parentColumns = ["id"],
        childColumns  = ["vehicleId"],
        onDelete      = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class ServiceTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vehicleId             : Int,
    val taskDescription       : String,
    val category              : TaskCategory,
    val isCompleted           : Boolean = false,
    val completedByMechanicId : Int?    = null,
    val mechanicNotes         : String  = "",
    val completedAt           : String? = null
)

enum class TaskCategory {
    ENGINE_POWERTRAIN, TYRES_WHEELS, BRAKES,
    FLUIDS, FILTERS, DIAGNOSTICS, ELECTRICAL, OTHER
}
