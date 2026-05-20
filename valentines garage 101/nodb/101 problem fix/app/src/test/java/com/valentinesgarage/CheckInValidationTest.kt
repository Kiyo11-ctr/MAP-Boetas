package com.valentinesgarage

import com.valentinesgarage.data.model.VehicleCondition
import org.junit.Assert.*
import org.junit.Test

class CheckInValidationTest {

    @Test fun `valid odometer passes`()          = assertTrue(validOdometer("128450"))
    @Test fun `zero odometer fails`()            = assertFalse(validOdometer("0"))
    @Test fun `negative odometer fails`()        = assertFalse(validOdometer("-500"))
    @Test fun `blank odometer fails`()           = assertFalse(validOdometer(""))
    @Test fun `non-numeric odometer fails`()     = assertFalse(validOdometer("abc"))
    @Test fun `valid registration passes`()      = assertTrue(validReg("N 4521 WH"))
    @Test fun `blank registration fails`()       = assertFalse(validReg(""))
    @Test fun `spaces-only registration fails`() = assertFalse(validReg("   "))
    @Test fun `good vehicle no notes ok`()       = assertTrue(notesValid("", VehicleCondition.GOOD))
    @Test fun `critical vehicle needs notes`()   = assertFalse(notesValid("", VehicleCondition.CRITICAL))
    @Test fun `critical with notes passes`()     = assertTrue(notesValid("Engine knock", VehicleCondition.CRITICAL))

    private fun validOdometer(v: String) = v.toIntOrNull()?.let { it > 0 } ?: false
    private fun validReg(v: String)      = v.isNotBlank()
    private fun notesValid(notes: String, c: VehicleCondition) =
        if (c == VehicleCondition.CRITICAL) notes.isNotBlank() else true
}
