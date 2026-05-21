package com.valentinesgarage

import com.valentinesgarage.data.model.Employee
import com.valentinesgarage.data.model.EmployeeReport
import com.valentinesgarage.data.model.EmployeeRole
import org.junit.Assert.*
import org.junit.Test

class EmployeeReportTest {

    private val emp = Employee(id = 1, fullName = "Johannes K.", role = EmployeeRole.LEAD_MECHANIC)

    @Test fun `100 percent all done`()       = assertEquals(100, report(10, 10).completionPercent)
    @Test fun `0 percent none assigned`()    = assertEquals(0,   report(0,  0).completionPercent)
    @Test fun `0 percent none complete`()    = assertEquals(0,   report(0,  5).completionPercent)
    @Test fun `80 percent for 4 of 5`()      = assertEquals(80,  report(4,  5).completionPercent)
    @Test fun `never exceeds 100`()          = assertTrue(report(5, 5).completionPercent <= 100)

    private fun report(done: Int, total: Int) =
        EmployeeReport(emp, tasksCompleted = done, totalTasksAssigned = total, vehiclesWorkedOn = 1)
}
