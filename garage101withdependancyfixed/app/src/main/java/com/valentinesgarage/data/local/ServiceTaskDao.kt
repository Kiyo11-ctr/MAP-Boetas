package com.valentinesgarage.data.local

import androidx.room.*
import com.valentinesgarage.data.model.ServiceTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: ServiceTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<ServiceTask>)

    @Update
    suspend fun update(task: ServiceTask)

    @Query("SELECT * FROM service_tasks WHERE completedByMechanicId = :mechanicId AND isCompleted = 1")
    fun getByMechanic(mechanicId: Int): Flow<List<ServiceTask>>
}
