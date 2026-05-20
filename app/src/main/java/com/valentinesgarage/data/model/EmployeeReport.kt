package com.valentinesgarage.data.model

/**
 * Non-persisted report model — assembled in the repository layer by joining
 * Employee and ServiceTask data. Shown on the Reports screen for Valentine.
 */
data class EmployeeReport(
    val employee: Employee,
    val tasksCompleted: Int,
    val totalTasksAssigned: Int,
    val vehiclesWorkedOn: Int
) {
    /** Completion percentage, safe against division by zero. */
    val completionPercent: Int
        get() = if (totalTasksAssigned == 0) 0
                else (tasksCompleted * 100) / totalTasksAssigned
}
