package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val registrationNumber: String,
    val makeAndModel: String,
    val driverName: String,
    val odometerAtCheckin: Int,
    val conditionAtCheckin: VehicleCondition,
    val conditionNotes: String,
    val assignedMechanicId: Int,
    val priority: ServicePriority,
    val checkinDateTime: String,
    val status: VehicleStatus = VehicleStatus.CHECKED_IN
)

enum class VehicleCondition { GOOD, FAIR, CRITICAL }
enum class ServicePriority   { NORMAL, HIGH, URGENT }
enum class VehicleStatus     { CHECKED_IN, IN_PROGRESS, COMPLETED }
