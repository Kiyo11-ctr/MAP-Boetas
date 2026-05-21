package com.valentinesgarage

import com.valentinesgarage.data.model.VehicleCondition
import org.junit.Assert.*
import org.junit.Test

class CheckInValidationTest {

    @Test fun `valid odometer passes`()          = assertTrue(validKm("128450"))
    @Test fun `zero odometer fails`()            = assertFalse(validKm("0"))
    @Test fun `negative odometer fails`()        = assertFalse(validKm("-500"))
    @Test fun `blank odometer fails`()           = assertFalse(validKm(""))
    @Test fun `non-numeric odometer fails`()     = assertFalse(validKm("abc"))
    @Test fun `valid registration passes`()      = assertTrue(validReg("N 4521 WH"))
    @Test fun `blank registration fails`()       = assertFalse(validReg(""))
    @Test fun `spaces-only registration fails`() = assertFalse(validReg("   "))
    @Test fun `good condition no notes ok`()     = assertTrue(notesOk("", VehicleCondition.GOOD))
    @Test fun `critical no notes fails`()        = assertFalse(notesOk("", VehicleCondition.CRITICAL))
    @Test fun `critical with notes passes`()     = assertTrue(notesOk("Engine knock", VehicleCondition.CRITICAL))

    private fun validKm(v: String)   = v.toIntOrNull()?.let { it > 0 } ?: false
    private fun validReg(v: String)  = v.isNotBlank()
    private fun notesOk(notes: String, c: VehicleCondition) =
        if (c == VehicleCondition.CRITICAL) notes.isNotBlank() else true
}
