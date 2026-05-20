package com.valentinesgarage.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Vehicle entity — represents a truck that has been checked into the garage.
 * Stored in the local Room database as the single source of truth.
 *
 * Capturing [conditionAtCheckin] and [odometerAtCheckin] at the moment of
 * arrival prevents disputes about pre-existing damage and detects whether
 * vehicles were driven while in the garage.
 */
@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val registrationNumber: String,       // e.g. "N 4521 WH"
    val makeAndModel: String,             // e.g. "Scania R500"
    val driverName: String,
    val odometerAtCheckin: Int,           // km recorded at check-in
    val conditionAtCheckin: VehicleCondition,
    val conditionNotes: String,
    val assignedMechanicId: Int,
    val priority: ServicePriority,
    val checkinDateTime: String,          // ISO-8601 string for Room storage
    val status: VehicleStatus = VehicleStatus.CHECKED_IN
)

enum class VehicleCondition { GOOD, FAIR, CRITICAL }

enum class ServicePriority { NORMAL, HIGH, URGENT }

enum class VehicleStatus { CHECKED_IN, IN_PROGRESS, COMPLETED }
