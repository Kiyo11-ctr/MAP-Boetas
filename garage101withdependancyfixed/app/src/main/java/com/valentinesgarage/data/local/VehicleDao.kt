package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.Vehicle
import com.valentinesgarage.data.model.VehicleWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle): Long

    @Update
    suspend fun update(vehicle: Vehicle)

    @Transaction
    @Query("SELECT * FROM vehicles ORDER BY checkinDateTime DESC")
    fun getAllWithTasks(): Flow<List<VehicleWithTasks>>
}
