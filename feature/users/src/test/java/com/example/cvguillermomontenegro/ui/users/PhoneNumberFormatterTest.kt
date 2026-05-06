package com.example.cvguillermomontenegro.ui.users

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberFormatterTest {

    @Test
    fun normalize_removesNonDigits_andLimitsLength() {
        val normalized = PhoneNumberFormatter.normalize("+54 (9) 11 2345-6789 ext 000")

        assertEquals("5491123456789", normalized)
    }

    @Test
    fun format_appliesExpectedGrouping() {
        val formatted = PhoneNumberFormatter.format("5491123456789")

        assertEquals("+54-9-11-2345-6789", formatted)
    }

    @Test
    fun offsetConversions_matchFormattedStructure() {
        val digits = "5491123456789"

        assertEquals(0, PhoneNumberFormatter.transformedToOriginal(1, digits))
        assertEquals(2, PhoneNumberFormatter.transformedToOriginal(3, digits))
        assertEquals(1, PhoneNumberFormatter.originalToTransformed(0, digits))
        assertEquals(3, PhoneNumberFormatter.originalToTransformed(2, digits))
        assertEquals(18, PhoneNumberFormatter.originalToTransformed(13, digits))
    }
}
