package com.valentinesgarage

import com.valentinesgarage.data.model.VehicleCondition
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for check-in form validation logic.
 *
 * These tests verify the boundary conditions for the odometer input and
 * confirm required-field validation works correctly.
 * Tests run on JVM without a device — fast feedback as taught in the course.
 */
class CheckInValidationTest {

    // ---- Odometer validation ----

    @Test
    fun `valid odometer returns true`() {
        assertTrue(isValidOdometer("128450"))
    }

    @Test
    fun `zero odometer returns false`() {
        assertFalse(isValidOdometer("0"))
    }

    @Test
    fun `negative odometer returns false`() {
        assertFalse(isValidOdometer("-1000"))
    }

    @Test
    fun `blank odometer returns false`() {
        assertFalse(isValidOdometer(""))
    }

    @Test
    fun `non-numeric odometer returns false`() {
        assertFalse(isValidOdometer("abc"))
    }

    // ---- Registration validation ----

    @Test
    fun `valid namibian registration passes`() {
        assertTrue(isValidRegistration("N 4521 WH"))
    }

    @Test
    fun `blank registration fails`() {
        assertFalse(isValidRegistration(""))
    }

    @Test
    fun `whitespace-only registration fails`() {
        assertFalse(isValidRegistration("   "))
    }

    // ---- Condition note length ----

    @Test
    fun `condition notes can be empty for good vehicles`() {
        assertTrue(conditionNotesValid("", VehicleCondition.GOOD))
    }

    @Test
    fun `critical vehicle without notes returns false`() {
        assertFalse(conditionNotesValid("", VehicleCondition.CRITICAL))
    }

    @Test
    fun `critical vehicle with notes returns true`() {
        assertTrue(conditionNotesValid("Engine knocking, oil leak", VehicleCondition.CRITICAL))
    }

    // Helper functions mirroring ViewModel validation
    private fun isValidOdometer(input: String): Boolean =
        input.toIntOrNull()?.let { it > 0 } ?: false

    private fun isValidRegistration(input: String): Boolean = input.isNotBlank()

    private fun conditionNotesValid(notes: String, condition: VehicleCondition): Boolean =
        if (condition == VehicleCondition.CRITICAL) notes.isNotBlank() else true
}
