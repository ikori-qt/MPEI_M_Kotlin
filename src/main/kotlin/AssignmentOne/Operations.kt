package AssignmentOne

class Addition : Operation {
    override fun execute(a: Int, b: Int): Int {
        return a + b
    }
}

class Subtraction : Operation {
    override fun execute(a: Int, b: Int): Int {
        return a - b
    }
}

class Multiplication : Operation {
    override fun execute(a: Int, b: Int): Int {
        return a * b
    }
}

class Division : Operation {
    override fun execute(a: Int, b: Int): Int {
        return a / b
    }
}