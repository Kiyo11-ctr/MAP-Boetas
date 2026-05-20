package com.valentinesgarage

import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeReport
import com.valentinesgarage.data.model.EmployeeRole
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for EmployeeReport computed properties.
 * Verifies that completion percentage calculations are correct and
 * safe against division-by-zero edge cases.
 */
class EmployeeReportTest {

    private val mechanic = Employee(id = 1, fullName = "Johannes K.", role = EmployeeRole.LEAD_MECHANIC)

    @Test
    fun `100 percent when all tasks complete`() {
        val report = EmployeeReport(mechanic, tasksCompleted = 10, totalTasksAssigned = 10, vehiclesWorkedOn = 2)
        assertEquals(100, report.completionPercent)
    }

    @Test
    fun `0 percent when no tasks assigned`() {
        val report = EmployeeReport(mechanic, tasksCompleted = 0, totalTasksAssigned = 0, vehiclesWorkedOn = 0)
        assertEquals(0, report.completionPercent)
    }

    @Test
    fun `0 percent when assigned but none completed`() {
        val report = EmployeeReport(mechanic, tasksCompleted = 0, totalTasksAssigned = 5, vehiclesWorkedOn = 1)
        assertEquals(0, report.completionPercent)
    }

    @Test
    fun `80 percent for 4 of 5 tasks complete`() {
        val report = EmployeeReport(mechanic, tasksCompleted = 4, totalTasksAssigned = 5, vehiclesWorkedOn = 1)
        assertEquals(80, report.completionPercent)
    }

    @Test
    fun `completed tasks cannot exceed total`() {
        val report = EmployeeReport(mechanic, tasksCompleted = 5, totalTasksAssigned = 5, vehiclesWorkedOn = 1)
        assertTrue(report.completionPercent <= 100)
    }
}
