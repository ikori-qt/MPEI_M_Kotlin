package AssignmentOne

object NumberIdentifier {
    // object instead of class was used to receive static properties
    fun isRomanNumber (
        number: String
    ): Boolean {
        // Will eat things like IIIIIV
        return Regex("[MDCLXVI]+").matches(number)
    }

    fun isArabicNumber (
        number: String
    ): Boolean {
        return Regex("[-,.0-9]+").matches(number)
    }
}