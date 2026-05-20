package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.Vehicle
import com.valentinesgarage.data.model.VehicleStatus
import com.valentinesgarage.data.model.VehicleWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: Vehicle): Long

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY checkinDateTime DESC")
    fun getAllVehiclesWithTasks(): Flow<List<VehicleWithTasks>>

    @Transaction
    @Query("SELECT * FROM vehicles WHERE id = :vehicleId")
    fun getVehicleWithTasks(vehicleId: Int): Flow<VehicleWithTasks?>

    @Query("SELECT COUNT(*) FROM vehicles")
    fun getTotalCount(): Flow<Int>
}
