package com.valentinesgarage.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.valentinesgarage.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database — the single source of truth for all garage data.
 *
 * Type converters handle Kotlin enums, since Room stores everything as
 * primitives. The database is pre-populated with seed data (employees and
 * default service tasks) via a [RoomDatabase.Callback].
 */
@Database(
    entities = [Vehicle::class, ServiceTask::class, Employee::class, User::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(GarageConverters::class)
abstract class GarageDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun serviceTaskDao(): ServiceTaskDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "garage_db"

        fun create(context: Context): GarageDatabase =
            Room.databaseBuilder(context, GarageDatabase::class.java, DATABASE_NAME)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default employees on first run
                        CoroutineScope(Dispatchers.IO).launch {
                            db.execSQL("""
                                INSERT INTO employees (fullName, role, isActive) VALUES
                                ('Valentine Mutorwa', 'MANAGER', 1),
                                ('Johannes Kavendjii', 'LEAD_MECHANIC', 1),
                                ('Petrus Nangolo', 'SENIOR_MECHANIC', 1),
                                ('Maria Shikongo', 'SENIOR_MECHANIC', 1),
                                ('Festus Haimbodi', 'JUNIOR_MECHANIC', 1),
                                ('Absalom Tjiueza', 'JUNIOR_MECHANIC', 1)
                            """)
                        }
                    }
                })
                .build()
    }
}

/** Converts enums to/from their string names for Room storage. */
class GarageConverters {
    @TypeConverter fun conditionToString(v: VehicleCondition): String = v.name
    @TypeConverter fun stringToCondition(v: String): VehicleCondition = VehicleCondition.valueOf(v)

    @TypeConverter fun priorityToString(v: ServicePriority): String = v.name
    @TypeConverter fun stringToPriority(v: String): ServicePriority = ServicePriority.valueOf(v)

    @TypeConverter fun statusToString(v: VehicleStatus): String = v.name
    @TypeConverter fun stringToStatus(v: String): VehicleStatus = VehicleStatus.valueOf(v)

    @TypeConverter fun categoryToString(v: TaskCategory): String = v.name
    @TypeConverter fun stringToCategory(v: String): TaskCategory = TaskCategory.valueOf(v)

    @TypeConverter fun roleToString(v: EmployeeRole): String = v.name
    @TypeConverter fun stringToRole(v: String): EmployeeRole = EmployeeRole.valueOf(v)
}
