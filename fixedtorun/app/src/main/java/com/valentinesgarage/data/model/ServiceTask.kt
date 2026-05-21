package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ServiceTask entity — a single item on the service checklist for a vehicle.
 *
 * Each task can be independently ticked off by any mechanic, who must also
 * record their name and notes. This collaborative model prevents tasks from
 * going undone because one mechanic assumed another had done it.
 *
 * Foreign key to [Vehicle] ensures tasks are deleted when a vehicle record
 * is removed (cascading delete).
 */
@Entity(
    tableName = "service_tasks",
    foreignKeys = [
        ForeignKey(
            entity = Vehicle::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class ServiceTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val vehicleId: Int,
    val taskDescription: String,
    val category: TaskCategory,
    val isCompleted: Boolean = false,
    val completedByMechanicId: Int? = null,   // null until ticked off
    val mechanicNotes: String = "",
    val completedAt: String? = null            // ISO-8601 timestamp
)

enum class TaskCategory {
    ENGINE_POWERTRAIN,
    TYRES_WHEELS,
    BRAKES,
    FLUIDS,
    FILTERS,
    DIAGNOSTICS,
    ELECTRICAL,
    BODYWORK,
    OTHER
}
