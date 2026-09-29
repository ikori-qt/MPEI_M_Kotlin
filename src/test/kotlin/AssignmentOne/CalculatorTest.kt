package AssignmentOne

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatorTest {
    @Test
    fun additionsWorks() {
        val testedOperation = Addition()
        assertEquals(10, testedOperation.execute(4,6))
    }

    @Test
    fun subtractionWorks() {
        val testedOperation = Subtraction()
        assertEquals(1, testedOperation.execute(7,6))
    }

    @Test
    fun divisionWorks() {
        val testedOperation = Division()
        assertEquals(5, testedOperation.execute(20,4))
    }

    @Test
    fun multiplicationWorks() {
        val testedOperation = Multiplication()
        assertEquals(70, testedOperation.execute(7,10))
    }

    @Test
    fun differsAreRejected() {
        assertEquals(false, (NumberIdentifier.isArabicNumber("1")
                && NumberIdentifier.isArabicNumber("V")))
    }

    @Test
    fun normalization() {
        assertEquals(1, RomanCalculations.normalize("I"))
        assertEquals(4, RomanCalculations.normalize("IV"))
        assertEquals(6, RomanCalculations.normalize("VI"))
        assertEquals(10, RomanCalculations.normalize("X"))
        assertEquals(49, RomanCalculations.normalize("XLIX"))
    }

    @Test
    fun intToRoman() {
        assertEquals("X", RomanCalculations.intToRoman(10))
        assertEquals("IV", RomanCalculations.intToRoman(4))
        assertEquals("VI", RomanCalculations.intToRoman(6))
        assertEquals("XLIX", RomanCalculations.intToRoman(49))
    }


    @Test
    fun lettersRejected() {
        assertEquals(false, NumberIdentifier.isRomanNumber("AXIC"))
    }

}