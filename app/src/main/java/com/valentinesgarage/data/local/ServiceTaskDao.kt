package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.ServiceTask
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ServiceTask operations.
 * Mechanics use [completeTask] to tick off items; managers use
 * [getTasksByMechanic] to view per-employee activity reports.
 */
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

    /**
     * Returns tasks completed by a specific mechanic — used for the
     * per-employee report that Valentine reviews.
     */
    @Query("""
        SELECT * FROM service_tasks 
        WHERE completedByMechanicId = :mechanicId AND isCompleted = 1
        ORDER BY completedAt DESC
    """)
    fun getTasksByMechanic(mechanicId: Int): Flow<List<ServiceTask>>

    @Query("SELECT COUNT(*) FROM service_tasks WHERE vehicleId = :vehicleId AND isCompleted = 1")
    fun getCompletedTaskCount(vehicleId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM service_tasks WHERE vehicleId = :vehicleId")
    fun getTotalTaskCount(vehicleId: Int): Flow<Int>
}
