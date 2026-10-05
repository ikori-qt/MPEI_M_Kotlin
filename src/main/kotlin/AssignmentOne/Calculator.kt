package AssignmentOne

import java.util.Scanner
import kotlin.collections.listOf

fun main () {
    val scanner: Scanner = Scanner(System.`in`)
    val rawInput: String = scanner.nextLine()

    val inputValues: List<String> = rawInput.split(Regex("[-+*/]"))
    val operator: String = Regex("[-+*/]")
        .find(rawInput)!!
        .value

    val bothArabic: Boolean = (NumberIdentifier.isArabicNumber(inputValues[0])
            && NumberIdentifier.isArabicNumber(inputValues[1]))
    val bothRoman: Boolean = (NumberIdentifier.isRomanNumber(inputValues[0])
            && NumberIdentifier.isRomanNumber(inputValues[1]))
    if (!(bothArabic || bothRoman)) {
        println("Supplied numbers differ by type")
    }

    if (bothRoman) {
        val printableResult: Int? = when (operator) {
            "+" -> RomanCalculations.addition(
                RomanCalculations.normalize(inputValues[0]),
                RomanCalculations.normalize(inputValues[1])
            )

            "-" -> RomanCalculations.subtraction(
                RomanCalculations.normalize(inputValues[0]),
                RomanCalculations.normalize(inputValues[1])
            )

            "*" -> RomanCalculations.multiplication(
                RomanCalculations.normalize(inputValues[0]),
                RomanCalculations.normalize(inputValues[1])
            )

            "/" -> RomanCalculations.division(
                RomanCalculations.normalize(inputValues[0]),
                RomanCalculations.normalize(inputValues[1])
            )
            else ->
                null
        }

        printableResult ?: println("Unknown operator supplied and somehow passed (impossible)")

        printableResult?.let{
            if (printableResult < 0 ) println("There are no negative Roman Numerals")
            else  println("Answer is " + RomanCalculations.intToRoman(printableResult))
        }

    } else if (bothArabic){
        val printableResult: Int? = when (operator) {
            "+" -> ArabicCalculations.addition(
                inputValues[0].toInt(),
                inputValues[1].toInt()
            )

            "-" -> RomanCalculations.subtraction(
                inputValues[0].toInt(),
                inputValues[1].toInt()
            )

            "*" -> RomanCalculations.multiplication(
                inputValues[0].toInt(),
                inputValues[1].toInt()
            )

            "/" -> RomanCalculations.division(
                inputValues[0].toInt(),
                inputValues[1].toInt()
            )
            else ->
                null
        }

        printableResult ?: println("Unknown operator supplied and somehow passed (impossible)")

        printableResult?.let{
            println("Answer is " + printableResult)
        }
    }
}

object ArabicCalculations: Calculations() {
    private val additionOp: Operation = Addition()
    private val subtractionOp: Operation = Subtraction()
    private val divisionOp: Operation = Division()
    private val multiplicationOp: Operation = Multiplication()

    /**
     * I've figured out how to change this comment's appearance (Ctrl+Alt+Q)
     */
    override fun addition (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return additionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun subtraction (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return subtractionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun division (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return divisionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun multiplication (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return multiplicationOp.execute(normalizedCompOne,normalizedCompTwo)
    }
}

object RomanCalculations: Calculations() {
    private val additionOp: Operation = Addition()
    private val subtractionOp: Operation = Subtraction()
    private val divisionOp: Operation = Division()
    private val multiplicationOp: Operation = Multiplication()
    private val transitionToInt: Map<Char, Int> = mapOf<Char, Int>(
        'I' to 1,
        'V' to 5,
        'X' to 10,
        'L' to 50,
        'C' to 100
    )
    // List of Pair<Int, String>
    private val transitionToRoman = listOf(
        100 to "C",
        90 to "XC",
        50 to "L",
        40 to "XL",
        10 to "X",
        9 to "IX",
        5 to "V",
        4 to "IV",
        1 to "I"
    )

    override fun addition (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return additionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun subtraction (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return subtractionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun division (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return divisionOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    override fun multiplication (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int {
        return multiplicationOp.execute(normalizedCompOne,normalizedCompTwo)
    }

    fun normalize (
        startingString: String
    ): Int {
        var sum: Int = 0
        for (i in startingString.indices) {
            // !! here asserts that we receive a noot null (meaning there is an Int for every possible incoming key Char)
            val current: Int = transitionToInt[startingString[i]]!!
            val next: Int = if (i >= startingString.lastIndex) {
                0
            } else {
                transitionToInt[startingString[i+1]]!!
            }

            if (current > next) {
                sum += current
            } else {
                sum -= current
            }
        }

        return sum
    }

    fun intToRoman(
        number: Int
    ): String {
        var leftover = number
        val result = StringBuilder()

        for ((arabic, roman) in transitionToRoman) {
            while (leftover >= arabic) {
                leftover -= arabic
                result.append(roman)
            }
        }

        return result.toString()
    }
}