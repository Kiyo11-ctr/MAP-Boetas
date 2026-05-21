package com.valentinesgarage.data.model

data class EmployeeReport(
    val employee           : Employee,
    val tasksCompleted     : Int,
    val totalTasksAssigned : Int,
    val vehiclesWorkedOn   : Int
) {
    val completionPercent: Int
        get() = if (totalTasksAssigned == 0) 0
                else (tasksCompleted * 100) / totalTasksAssigned
}
