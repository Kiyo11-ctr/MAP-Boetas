package com.valentinesgarage.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.ServiceTask
import com.valentinesgarage.data.model.Vehicle

@Database(
    entities    = [Vehicle::class, ServiceTask::class, Employee::class],
    version     = 1,
    exportSchema = false
)
@TypeConverters(GarageConverters::class)
abstract class GarageDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceTaskDao(): ServiceTaskDao
    abstract fun employeeDao(): EmployeeDao

    companion object {
        fun build(context: Context): GarageDatabase =
            Room.databaseBuilder(context, GarageDatabase::class.java, "garage_db").build()
    }
}
