package com.valentinesgarage.data.local

import android.content.Context
import androidx.room.*
import com.valentinesgarage.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Vehicle::class, ServiceTask::class, Employee::class],
    version  = 1,
    exportSchema = false
)
@TypeConverters(GarageConverters::class)
abstract class GarageDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceTaskDao(): ServiceTaskDao
    abstract fun employeeDao(): EmployeeDao

    companion object {
        const val DB_NAME = "garage_db"

        fun build(context: Context): GarageDatabase =
            Room.databaseBuilder(context, GarageDatabase::class.java, DB_NAME)
                .build()
    }
}

class GarageConverters {
    @TypeConverter fun cond2str(v: VehicleCondition): String = v.name
    @TypeConverter fun str2cond(v: String): VehicleCondition = VehicleCondition.valueOf(v)
    @TypeConverter fun pri2str(v: ServicePriority): String = v.name
    @TypeConverter fun str2pri(v: String): ServicePriority = ServicePriority.valueOf(v)
    @TypeConverter fun stat2str(v: VehicleStatus): String = v.name
    @TypeConverter fun str2stat(v: String): VehicleStatus = VehicleStatus.valueOf(v)
    @TypeConverter fun cat2str(v: TaskCategory): String = v.name
    @TypeConverter fun str2cat(v: String): TaskCategory = TaskCategory.valueOf(v)
    @TypeConverter fun role2str(v: EmployeeRole): String = v.name
    @TypeConverter fun str2role(v: String): EmployeeRole = EmployeeRole.valueOf(v)
}
