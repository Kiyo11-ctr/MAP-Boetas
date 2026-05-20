package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.ServiceTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceTaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ServiceTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<ServiceTask>)

    @Update
    suspend fun updateTask(task: ServiceTask)

    @Query("SELECT * FROM service_tasks WHERE vehicleId = :vehicleId ORDER BY id ASC")
    fun getTasksForVehicle(vehicleId: Int): Flow<List<ServiceTask>>

    @Query("SELECT * FROM service_tasks WHERE completedByMechanicId = :mechanicId AND isCompleted = 1")
    fun getTasksByMechanic(mechanicId: Int): Flow<List<ServiceTask>>
}
