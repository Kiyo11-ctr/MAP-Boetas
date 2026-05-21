package com.valentinesgarage.data.local

import androidx.room.TypeConverter
import com.valentinesgarage.data.model.*

class GarageConverters {
    @TypeConverter fun condToStr(v: VehicleCondition): String = v.name
    @TypeConverter fun strToCond(v: String): VehicleCondition = VehicleCondition.valueOf(v)
    @TypeConverter fun priToStr(v: ServicePriority): String = v.name
    @TypeConverter fun strToPri(v: String): ServicePriority = ServicePriority.valueOf(v)
    @TypeConverter fun statToStr(v: VehicleStatus): String = v.name
    @TypeConverter fun strToStat(v: String): VehicleStatus = VehicleStatus.valueOf(v)
    @TypeConverter fun catToStr(v: TaskCategory): String = v.name
    @TypeConverter fun strToCat(v: String): TaskCategory = TaskCategory.valueOf(v)
    @TypeConverter fun roleToStr(v: EmployeeRole): String = v.name
    @TypeConverter fun strToRole(v: String): EmployeeRole = EmployeeRole.valueOf(v)
}
