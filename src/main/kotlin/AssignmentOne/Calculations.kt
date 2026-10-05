package AssignmentOne

abstract class Calculations {
    abstract fun addition (
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int
    abstract fun subtraction(
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int
    abstract fun division(
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int
    abstract fun multiplication(
        normalizedCompOne: Int,
        normalizedCompTwo: Int
    ): Int
}
