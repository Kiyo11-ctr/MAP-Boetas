package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.Vehicle
import com.valentinesgarage.data.model.VehicleStatus
import com.valentinesgarage.data.model.VehicleWithTasks
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Vehicle operations.
 * All queries return Flow<T> so the UI layer automatically
 * reacts to database changes (Unidirectional Data Flow).
 */
@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Delete
    suspend fun deleteVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles ORDER BY checkinDateTime DESC")
    fun getAllVehicles(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun getVehicleById(id: Int): Flow<Vehicle?>

    @Query("SELECT * FROM vehicles WHERE status = :status ORDER BY checkinDateTime DESC")
    fun getVehiclesByStatus(status: VehicleStatus): Flow<List<Vehicle>>

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY checkinDateTime DESC")
    fun getAllVehiclesWithTasks(): Flow<List<VehicleWithTasks>>

    @Transaction
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    fun getVehicleWithTasks(vehicleId: Int): Flow<VehicleWithTasks?>

    @Query("SELECT COUNT(*) FROM vehicles WHERE date(checkinDateTime) = date('now')")
    fun getTodayCheckinCount(): Flow<Int>
}
